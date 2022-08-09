package com.abhishek.notificationservice.controller;

import com.abhishek.notificationservice.model.ErrorResponse;
import com.abhishek.notificationservice.model.PhoneNumberPayload;
import com.abhishek.notificationservice.model.Response;
import com.abhishek.notificationservice.model.entity.mysql.PhoneNumber;
import com.abhishek.notificationservice.repository.PhoneNumberRepository;
import com.abhishek.notificationservice.repository.RedisRepository;
import com.abhishek.notificationservice.service.PhoneNumberService;
import com.abhishek.notificationservice.util.enums.PhoneNumberStatusEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class PhoneNumberController {

    RedisRepository redisRepository;
    PhoneNumberService phoneNumberService;


    public PhoneNumberController(RedisRepository redisRepository, PhoneNumberService phoneNumberService) {
        this.redisRepository = redisRepository;
        this.phoneNumberService = phoneNumberService;
    }

    @PostMapping("/v1/blacklist")
    public ResponseEntity<Response> blackListNumber(@RequestBody PhoneNumberPayload phoneNumberPayload){

        Response response = new Response();
        try {
            phoneNumberPayload.getPhoneNumbers().forEach(phoneNumber -> {

                redisRepository.blackListPhoneNumber(phoneNumber);
                PhoneNumber phoneNumber1 = new PhoneNumber();
                phoneNumber1.setPhoneNumber(phoneNumber);
                phoneNumber1.setStatus(PhoneNumberStatusEnum.BLACKLISTED);
                phoneNumberService.updatePhoneNumber(phoneNumber1);

            });

            response.setData("Successfully blacklisted");

        } catch( Exception exception ) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setCode(String.valueOf(exception.hashCode()));
            errorResponse.setMessage(exception.getMessage());
            response.setError(errorResponse);
        }
        return new ResponseEntity<Response>( response, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/v1/blacklist")
    public ResponseEntity<Response> whiteListNumber( @RequestBody PhoneNumberPayload phoneNumberPayload){

        Response response = new Response();
        try {
            phoneNumberPayload.getPhoneNumbers().forEach(phoneNumber -> {

                redisRepository.whiteListPhoneNumber(phoneNumber);
                PhoneNumber phoneNumber1 = new PhoneNumber();
                phoneNumber1.setPhoneNumber(phoneNumber);
                phoneNumber1.setStatus(PhoneNumberStatusEnum.WHITELISTED);
                phoneNumberService.updatePhoneNumber(phoneNumber1);

            });
            response.setData("Successfully Whitelisted");
        } catch( Exception exception ){
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setCode(String.valueOf(exception.hashCode()));
            errorResponse.setMessage(exception.getMessage());
            response.setError(errorResponse);
        }
        return new ResponseEntity<Response>( response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/v1/blacklist")
    public ResponseEntity<Response> getBlackListedNumbers() {
        Response response = new Response();
        try {
            response.setData(phoneNumberService.getAllBlockedNumbers());
        }catch( Exception exception){
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setCode(String.valueOf(exception.hashCode()));
            errorResponse.setMessage(exception.getMessage());
            response.setError(errorResponse);
        }
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
