package com.abhishek.notificationservice.controller;

import com.abhishek.notificationservice.kafka.KafkaProducer;
import com.abhishek.notificationservice.model.PhoneNumberPayload;
import com.abhishek.notificationservice.model.entity.mysql.SmsRequest;
import com.abhishek.notificationservice.model.entity.redis.PhoneNumberRedis;
import com.abhishek.notificationservice.repository.RedisRepository;
import com.abhishek.notificationservice.service.SmsRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class SmsRequestController {

    private SmsRequestService smsRequestService;
    private KafkaProducer kafkaProducer;

    private RedisRepository redisRepository;

    public SmsRequestController(SmsRequestService smsRequestService, KafkaProducer kafkaProducer, RedisRepository redisRepository) {
        this.smsRequestService = smsRequestService;
        this.kafkaProducer = kafkaProducer;
        this.redisRepository = redisRepository;
    }

    @PostMapping("/v1/sms/send")
    public ResponseEntity<String> sendSms(@RequestBody SmsRequest smsRequest ){
        kafkaProducer.sendMessage(smsRequest);
        return smsRequestService.sendSms(smsRequest);
    }

    @PostMapping("/v1/blacklist")
    public ResponseEntity<String> blackListNumber(@RequestBody PhoneNumberPayload phoneNumberPayload){
        // add the phone number to redis
        // add the phone number to the MQSql DB
        // return appropriate response
        phoneNumberPayload.getPhoneNumbers().forEach(phoneNumber -> {
            PhoneNumberRedis phoneNumberRedis = new PhoneNumberRedis( phoneNumber,"sample");
            System.out.println(phoneNumberRedis);
            redisRepository.savePhoneNumber(phoneNumberRedis);
        });
        return new ResponseEntity<>("Success", HttpStatus.ACCEPTED);

    }

    @DeleteMapping("/v1/blacklist")
    public ResponseEntity<String> whiteListNumber( @RequestBody PhoneNumberPayload phoneNumberPayload){
        return new ResponseEntity<>("", HttpStatus.ACCEPTED);
    }

    @GetMapping("/v1/blacklist")
    public ResponseEntity<String> getBlackListedNumbers(){
        return new ResponseEntity<>("", HttpStatus.ACCEPTED);
    }

    @GetMapping("/v1/sms/{id}")
    public ResponseEntity<String> getSmsDetailsById(@PathVariable Long id){
        return new ResponseEntity<>("", HttpStatus.ACCEPTED);
    }

}
