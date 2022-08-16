package com.abhishek.notificationservice.controller;

import com.abhishek.notificationservice.model.SmsErrorResponse;
import com.abhishek.notificationservice.model.PhoneNumberPayload;
import com.abhishek.notificationservice.model.SmsResponse;
import com.abhishek.notificationservice.model.BlackListResponse;
import com.abhishek.notificationservice.model.entity.mysql.PhoneNumber;
import com.abhishek.notificationservice.service.PhoneNumberService;
import com.abhishek.notificationservice.service.RedisService;
import com.abhishek.notificationservice.utils.PhoneNumberHelper;
import com.abhishek.notificationservice.utils.enums.PhoneNumberStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Controller
@RequestMapping("/v1/blacklist")
public class PhoneNumberController {

    RedisService redisService;
    PhoneNumberService phoneNumberService;

    public PhoneNumberController(PhoneNumberService phoneNumberService, RedisService redisService) {
        this.phoneNumberService = phoneNumberService;
        this.redisService = redisService;
    }

    @PostMapping
    public ResponseEntity<BlackListResponse> blackListNumber(@RequestBody PhoneNumberPayload phoneNumberPayload) {

        BlackListResponse blackListResponse = new BlackListResponse();
        HttpStatus httpStatus = HttpStatus.ACCEPTED;
        try {
            phoneNumberPayload.getPhoneNumbers().forEach(phoneNumber -> {

                if (PhoneNumberHelper.isValidPhoneNumber(phoneNumber) == Boolean.FALSE) {
                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, String.format("Phone Number %s is not valid", phoneNumber));
                }
                redisService.blackListPhoneNumber(phoneNumber);
                PhoneNumber phoneNumber1 = new PhoneNumber();
                phoneNumber1.setPhoneNumber(phoneNumber);
                phoneNumber1.setStatus(PhoneNumberStatusEnum.BLACKLISTED);
                phoneNumberService.updatePhoneNumber(phoneNumber1);

            });

            blackListResponse.setData("Successfully blacklisted");

        } catch (HttpClientErrorException exception) {
            blackListResponse.setError(exception.getStatusText());
            httpStatus = exception.getStatusCode();
        } catch (Exception exception) {
            blackListResponse.setError(exception.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(blackListResponse, httpStatus);
    }


    @DeleteMapping
    public ResponseEntity<BlackListResponse> whiteListNumber(@RequestBody PhoneNumberPayload phoneNumberPayload) {

        BlackListResponse blackListResponse = new BlackListResponse();
        HttpStatus httpStatus = HttpStatus.ACCEPTED;
        try {
            phoneNumberPayload.getPhoneNumbers().forEach(phoneNumber -> {
                if (PhoneNumberHelper.isValidPhoneNumber(phoneNumber) == Boolean.FALSE) {
                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, String.format("Phone Number %s is not valid", phoneNumber));
                }
                redisService.whiteListPhoneNumber(phoneNumber);
                PhoneNumber phoneNumber1 = new PhoneNumber();
                phoneNumber1.setPhoneNumber(phoneNumber);
                phoneNumber1.setStatus(PhoneNumberStatusEnum.WHITELISTED);
                phoneNumberService.updatePhoneNumber(phoneNumber1);

            });
            blackListResponse.setData("Successfully Whitelisted");
        } catch (HttpClientErrorException exception) {
            blackListResponse.setError(exception.getStatusText());
            httpStatus = exception.getStatusCode();
        } catch (Exception exception) {
            blackListResponse.setError(exception.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(blackListResponse, httpStatus);
    }

    @GetMapping
    public ResponseEntity<BlackListResponse> getBlackListedNumbers() {

        BlackListResponse blackListResponse = new BlackListResponse();
        HttpStatus httpStatus = HttpStatus.ACCEPTED;
        try {
            blackListResponse.setData(phoneNumberService.getAllBlockedNumbers());
        } catch (Exception exception) {
            blackListResponse.setError(exception.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(blackListResponse, httpStatus);
    }
}
