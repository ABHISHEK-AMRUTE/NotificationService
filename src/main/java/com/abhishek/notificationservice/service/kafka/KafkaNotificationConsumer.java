package com.abhishek.notificationservice.service.kafka;

import com.abhishek.notificationservice.model.ImiConnect.request.ImiConnectRequest;
import com.abhishek.notificationservice.model.ImiConnect.response.ImiConnectResponse;
import com.abhishek.notificationservice.model.ImiConnect.response.ImiResponseBody;
import com.abhishek.notificationservice.model.entity.elasticSearch.SmsRequestESDocument;
import com.abhishek.notificationservice.model.entity.mysql.PhoneNumber;
import com.abhishek.notificationservice.model.entity.mysql.SmsRequest;
import com.abhishek.notificationservice.service.PhoneNumberService;
import com.abhishek.notificationservice.service.RedisService;
import com.abhishek.notificationservice.service.SmsRequestElasticService;
import com.abhishek.notificationservice.service.SmsRequestService;
import com.abhishek.notificationservice.utils.enums.PhoneNumberStatusEnum;
import com.abhishek.notificationservice.utils.enums.SmsStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class KafkaNotificationConsumer {

    @Value("${spring.imiConnect.key}")
    private String imiConnectKey;
    @Value("${spring.imiConnect.url}")
    private String imiConnectUrl;

    private PhoneNumberService phoneNumberService;
    private SmsRequestService smsRequestService;
    private SmsRequestElasticService smsRequestElasticService;
    private RedisService redisService;

    private RestTemplate restTemplate;

    public KafkaNotificationConsumer(PhoneNumberService phoneNumberService, SmsRequestService smsRequestService, SmsRequestElasticService smsRequestElasticService, RedisService redisService, RestTemplateBuilder restTemplateBuilder) {

        this.phoneNumberService = phoneNumberService;
        this.smsRequestService = smsRequestService;
        this.smsRequestElasticService = smsRequestElasticService;
        this.redisService = redisService;
        this.restTemplate = restTemplateBuilder.build();

    }

    /**
     * This function check in the redis ( with fall back mechanism ), is the current number is blacklisted or not.
     * @return The boolean value accordingly.
     */
    private Boolean getPhoneNumberBlockStatus(String phoneNumber){

        // Check if number is in the redis cache or not
        PhoneNumberStatusEnum redisResponse = redisService.getPhoneNumberStatus(phoneNumber);
        if( redisResponse == PhoneNumberStatusEnum.BLACKLISTED ) return true;
        else if( redisResponse == PhoneNumberStatusEnum.WHITELISTED ) return false;
         else {
             // If number is not in the redis cache, get it from DB and populate in redis cache.
            PhoneNumber phoneNumberFromDB = phoneNumberService.getPhoneNumber(phoneNumber);
            if( phoneNumberFromDB == null )
            {
                // If number is not present in SQL DB then insert it.
                phoneNumberFromDB = new PhoneNumber();
                phoneNumberFromDB.setPhoneNumber(phoneNumber);
                phoneNumberFromDB.setStatus(PhoneNumberStatusEnum.WHITELISTED);
                phoneNumberService.savePhoneNumber( phoneNumberFromDB);
            }
            // update the number in redis
            redisService.savePhoneNumber(phoneNumber);
            return false;
        }

    }

    /**
     * @param imiConnectRequest (Request body for the imi connect post request)
     * This function sends the sms notification to the specified phone number
     */
    private ImiConnectResponse sendSmsNotification(ImiConnectRequest imiConnectRequest){
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("key", imiConnectKey);

        HttpEntity<ImiConnectRequest> entity = new HttpEntity<>(imiConnectRequest, headers);
        ImiConnectResponse response = this.restTemplate.postForEntity(imiConnectUrl, entity, ImiConnectResponse.class).getBody();
        return response;
    }

    /**
     *
     * @param requestId id of the SmsRequest that has to be processed
     *  This consumer checks if the current number is blacklisted or not.
     *  Do not send sms to blocked numbers
     *  Update the SmsRequest accordingly
     */
    @KafkaListener(topics = "notification.send_sms", groupId = "myGroup")
    public void consume( Long requestId ){
        log.info("Message received: {}", requestId);

        try{

            // Get the SmRequest that has to be processed from the SQL DB
            SmsRequest message = smsRequestService.getSmsRequestById(requestId);

            // Check if the number is blocked or not
            Boolean isNumberBlackListed = getPhoneNumberBlockStatus(message.getPhoneNumber());

            if( isNumberBlackListed ){
                // If number is blacklisted set the appropriate response
                message.setStatus( SmsStatusEnum.FAILED );
                message.setFailure_comments("Number is blackListed");
                message.setFailure_code("NUMBER_BLACKLISTED");
            } else {

                // Prepare the body for sending the imiConnect post request
                ImiConnectRequest imiConnectRequest = new ImiConnectRequest();
                imiConnectRequest.getChannels().getSms().setText( message.getMessage() );
                imiConnectRequest.getDestination().get(0).setMsisdn(Arrays.asList(message.getPhoneNumber()));


                try {
                    ImiConnectResponse imiConnectResponse = sendSmsNotification(imiConnectRequest);
                    if( imiConnectResponse.getResponse() instanceof List<?>){
                        message.setStatus(SmsStatusEnum.SENT);
                     }else{
                        ImiResponseBody imiResponseBody = (ImiResponseBody) imiConnectResponse.getResponse();
                        message.setStatus(SmsStatusEnum.FAILED);
                        message.setFailure_code(String.valueOf(imiResponseBody.getCode()));
                        message.setFailure_comments(imiResponseBody.getDescription());
                    }
                }catch (Exception e) {
                    message.setStatus(SmsStatusEnum.FAILED);
                    message.setFailure_comments(e.getMessage());
                }
            }
            // save the sms to the DB
            smsRequestService.saveSmsRequest(message);

            //Index the SmsRequest document to the elastic search
            SmsRequestESDocument smsRequestESDocument = new SmsRequestESDocument(message);
            smsRequestElasticService.save(smsRequestESDocument);

            log.info( "Successfully processed the sms with requestId: {}", requestId );

        } catch( Exception exception) {
            log.info( "Error while processing  the sms with requestId: {}, due to: {}", requestId, exception.getMessage() );
        }
    }
}
