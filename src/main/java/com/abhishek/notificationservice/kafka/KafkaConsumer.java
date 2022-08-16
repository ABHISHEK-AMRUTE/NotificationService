package com.abhishek.notificationservice.kafka;

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

@Service
public class KafkaConsumer {

    private  static  final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @Value("${spring.imiConnect.key}")
    public String imiConnectKey;
    private PhoneNumberService phoneNumberService;
    private SmsRequestService smsRequestService;
    private SmsRequestElasticService smsRequestElasticService;
    private RedisService redisService;

    private RestTemplate restTemplate;

    public KafkaConsumer(PhoneNumberService phoneNumberService, SmsRequestService smsRequestService,  SmsRequestElasticService smsRequestElasticService, RedisService redisService,  RestTemplateBuilder restTemplateBuilder) {

        this.phoneNumberService = phoneNumberService;
        this.smsRequestService = smsRequestService;
        this.smsRequestElasticService = smsRequestElasticService;
        this.redisService = redisService;
        this.restTemplate = restTemplateBuilder.build();

    }

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

    private ImiConnectResponse sendSmsNotification(ImiConnectRequest imiConnectRequest){
        String url = "https://api.imiconnect.in/resources/v1/messaging";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("key", imiConnectKey);

        HttpEntity<ImiConnectRequest> entity = new HttpEntity<>(imiConnectRequest, headers);
        ImiConnectResponse response = this.restTemplate.postForEntity(url, entity, ImiConnectResponse.class).getBody();
        return response;
    }
    @KafkaListener(topics = "notification.send_sms", groupId = "myGroup")
    public void consume( Long requestId ){
        LOGGER.info(String.format("Message recieved %s", requestId));
        SmsRequest message = smsRequestService.getSmsRequestById(requestId);
        // check if the number is blocked or not
        Boolean isNumberBlackListed = getPhoneNumberBlockStatus(message.getPhoneNumber());

        if( isNumberBlackListed ){
            message.setStatus( SmsStatusEnum.FAILED );
            message.setFailure_comments("Number is blackListed");
            message.setFailure_code("NUMBER_BLACKLISTED");
        } else {

            ImiConnectRequest imiConnectRequest = new ImiConnectRequest();
            imiConnectRequest.getChannels().getSms().setText( message.getMessage() );
            imiConnectRequest.getDestination().get(0).setMsisdn(Arrays.asList(message.getPhoneNumber()));
            imiConnectRequest.getDestination().get(0).setCorrelationId("12345");


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
                System.out.println(e.getMessage());
            }
        }
        // save the sms to the DB
        smsRequestService.saveSmsRequest(message);

        //Index the SmsRequest document to the elastic search
        SmsRequestESDocument smsRequestESDocument = new SmsRequestESDocument(message);
        smsRequestElasticService.save(smsRequestESDocument);
    }
}
