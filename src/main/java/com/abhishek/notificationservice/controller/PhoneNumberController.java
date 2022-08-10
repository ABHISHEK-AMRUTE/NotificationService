package com.abhishek.notificationservice.controller;

import com.abhishek.notificationservice.model.ErrorResponse;
import com.abhishek.notificationservice.model.PhoneNumberPayload;
import com.abhishek.notificationservice.model.Response;
import com.abhishek.notificationservice.model.entity.mysql.PhoneNumber;
import com.abhishek.notificationservice.repository.RedisRepository;
import com.abhishek.notificationservice.service.PhoneNumberService;
import com.abhishek.notificationservice.service.RedisService;
import com.abhishek.notificationservice.utils.enums.PhoneNumberStatusEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class PhoneNumberController {

    RedisService redisService;
    PhoneNumberService phoneNumberService;


    public PhoneNumberController(PhoneNumberService phoneNumberService, RedisService redisService) {
        this.phoneNumberService = phoneNumberService;
        this.redisService = redisService;
    }

    @PostMapping("/v1/blacklist")
    public ResponseEntity<Response> blackListNumber(@RequestBody PhoneNumberPayload phoneNumberPayload){

        Response response = new Response();
        try {
            phoneNumberPayload.getPhoneNumbers().forEach(phoneNumber -> {

                redisService.blackListPhoneNumber(phoneNumber);
                PhoneNumber phoneNumber1 = new PhoneNumber();
                phoneNumber1.setPhoneNumber(phoneNumber);
                phoneNumber1.setStatus(PhoneNumberStatusEnum.BLACKLISTED);
                phoneNumberService.updatePhoneNumber(phoneNumber1);

            });

            response.setData("Successfully blacklisted");

        } catch( Exception exception ) {
            ErrorResponse errorResponse = new ErrorResponse(String.valueOf(exception.hashCode()), exception.getMessage());
            response.setError(errorResponse);
        }
        return new ResponseEntity<Response>( response, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/v1/blacklist")
    public ResponseEntity<Response> whiteListNumber( @RequestBody PhoneNumberPayload phoneNumberPayload){

        Response response = new Response();
        try {
            phoneNumberPayload.getPhoneNumbers().forEach(phoneNumber -> {

                redisService.whiteListPhoneNumber(phoneNumber);
                PhoneNumber phoneNumber1 = new PhoneNumber();
                phoneNumber1.setPhoneNumber(phoneNumber);
                phoneNumber1.setStatus(PhoneNumberStatusEnum.WHITELISTED);
                phoneNumberService.updatePhoneNumber(phoneNumber1);

            });
            response.setData("Successfully Whitelisted");
        } catch( Exception exception ){
            ErrorResponse errorResponse = new ErrorResponse(String.valueOf(exception.hashCode()), exception.getMessage());
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
            ErrorResponse errorResponse = new ErrorResponse(String.valueOf(exception.hashCode()), exception.getMessage());
            response.setError(errorResponse);
        }
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
