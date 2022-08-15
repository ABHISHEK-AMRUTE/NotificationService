package com.abhishek.notificationservice.kafka;

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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class KafkaConsumer {

    private  static  final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    private PhoneNumberService phoneNumberService;
    private SmsRequestService smsRequestService;
    private SmsRequestElasticService smsRequestElasticService;
    private RedisService redisService;

    public KafkaConsumer(PhoneNumberService phoneNumberService, SmsRequestService smsRequestService,  SmsRequestElasticService smsRequestElasticService, RedisService redisService) {

        this.phoneNumberService = phoneNumberService;
        this.smsRequestService = smsRequestService;
        this.smsRequestElasticService = smsRequestElasticService;
        this.redisService = redisService;

    }

    private Boolean getNumberBlockStatus(String phoneNumber){
        PhoneNumberStatusEnum redisResponse = redisService.getPhoneNumberStatus(phoneNumber);
        if( redisResponse == PhoneNumberStatusEnum.BLACKLISTED ) return true;
        else if( redisResponse == PhoneNumberStatusEnum.WHITELISTED ) return false;
         else {
             // cache miss or null response, get from db and update in redis
             // get number from db if present
            PhoneNumber phoneNumberFromDB = phoneNumberService.getPhoneNumber(phoneNumber);
            if( phoneNumberFromDB == null )
            {
                // Phone Number is not in the DB we will insert it

                phoneNumberFromDB = new PhoneNumber();
                phoneNumberFromDB.setPhoneNumber(phoneNumber);
                phoneNumberFromDB.setStatus(PhoneNumberStatusEnum.WHITELISTED);
                phoneNumberService.savePhoneNumber( phoneNumberFromDB);
                System.out.println("Here is the updated one");
                System.out.println(phoneNumberFromDB);
            }
            // update the number in redis
            redisService.savePhoneNumber(phoneNumber);
            return false;
        }

    }
    @KafkaListener(topics = "notification.send_sms", groupId = "myGroup")
    public void consume( Long requestId ){
        SmsRequest message = smsRequestService.getSmsRequestById(requestId);
        LOGGER.info(String.format("Message recieved %s", message));
        /**
         * Check if the number is in the redis ( populate if not found )
         * Check if the number is whitelisted or blacklisted
         * insert the phoneNumber in DB and redis if not found
         * save in SQL and send to SMS via third party
         */
        Boolean isNumberBlackListed = getNumberBlockStatus(message.getPhoneNumber());

        if( isNumberBlackListed ){
            message.setStatus( SmsStatusEnum.FAILED );
            message.setFailure_comments("Number is blackListed");
            message.setFailure_code("123");
        } else {
            /**
             * #todo Logic to send the SMS
             */
            message.setStatus( SmsStatusEnum.SENT );
        }
        message.setCreated_at(new Date());
        message.setUpdated_at(new Date());
        smsRequestService.saveSmsRequest(message);
        //index to the elastic search
        SmsRequestESDocument smsRequestESDocument = new SmsRequestESDocument(message);
         smsRequestElasticService.save(smsRequestESDocument);
    }
}
