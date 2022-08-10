package com.abhishek.notificationservice.controller;

import com.abhishek.notificationservice.kafka.KafkaProducer;
import com.abhishek.notificationservice.model.ErrorResponse;
import com.abhishek.notificationservice.model.PhoneNumberPayload;
import com.abhishek.notificationservice.model.Response;
import com.abhishek.notificationservice.model.SmsResponse;
import com.abhishek.notificationservice.model.entity.mysql.SmsRequest;
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


    public SmsRequestController(SmsRequestService smsRequestService, KafkaProducer kafkaProducer) {
        this.smsRequestService = smsRequestService;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/v1/sms/send")
    public ResponseEntity<Response> sendSms(@RequestBody SmsRequest smsRequest ){
        Response response = new Response();
        try {
            kafkaProducer.sendMessage( smsRequest );
            response.setData(new SmsResponse("1234", "Successfully sent"));
        } catch (Exception exception){
            ErrorResponse errorResponse = new ErrorResponse(String.valueOf(exception.hashCode()), exception.getMessage());
            response.setError(errorResponse);
        }
        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
    }

    @GetMapping("/v1/sms/{id}")
    public ResponseEntity<Response> getSmsDetailsById(@PathVariable Long id){
        Response response = new Response();

        try {
            response.setData( smsRequestService.getSmsRequestById(id) );
        }catch (Exception exception){
            ErrorResponse errorResponse = new ErrorResponse(String.valueOf(exception.hashCode()), exception.getMessage());
            response.setError(errorResponse);
        }
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

}
