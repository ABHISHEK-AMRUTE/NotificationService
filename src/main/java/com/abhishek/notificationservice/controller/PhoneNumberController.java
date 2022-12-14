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

    /**
     * @param phoneNumberPayload ( List of phoneNumbers )
     * Takes list of phone numbers as an input, and after validation, it blacklists the numbers.
     * Update the number both in Redis and SQL DB.
     */
    @PostMapping
    public ResponseEntity<BlackListResponse> blackListNumber(@RequestBody PhoneNumberPayload phoneNumberPayload) {

        BlackListResponse blackListResponse = new BlackListResponse();
        HttpStatus httpStatus = HttpStatus.ACCEPTED;
        try {
            phoneNumberPayload.getPhoneNumbers().forEach(phoneNumber -> {

                // Validation check for the phoneNumber
                if (PhoneNumberHelper.isValidPhoneNumber(phoneNumber) == Boolean.FALSE) {
                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, String.format("Phone Number %s is not valid", phoneNumber));
                }

                //update in redis
                redisService.blackListPhoneNumber(phoneNumber);

                //update in SQL DB
                PhoneNumber phoneNumber1 = new PhoneNumber();
                phoneNumber1.setPhoneNumber(phoneNumber);
                phoneNumber1.setStatus(PhoneNumberStatusEnum.BLACKLISTED);
                phoneNumberService.updatePhoneNumber(phoneNumber1);

            });

            blackListResponse.setData("Successfully blacklisted");

        } catch (HttpClientErrorException exception) {
            blackListResponse.setError(exception.getStatusText());
            httpStatus = exception.getStatusCode();
            log.info("Error while blacklisting numbers: {}, due to: {}", phoneNumberPayload, exception.getStatusText());
        } catch (Exception exception) {
            blackListResponse.setError(exception.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            log.info("Error while blacklisting numbers: {}, due to: {}", phoneNumberPayload, exception.getMessage());
        }
        log.info("Blacklisted all the numbers in: {}", phoneNumberPayload);
        return new ResponseEntity<>(blackListResponse, httpStatus);
    }

    /**
     * @param phoneNumberPayload ( List of phoneNumbers )
     * Takes list of phone numbers as an input, and after validation, it WhiteList the numbers.
     * Update the number both in Redis and SQL DB.
     */
    @DeleteMapping
    public ResponseEntity<BlackListResponse> whiteListNumber(@RequestBody PhoneNumberPayload phoneNumberPayload) {

        BlackListResponse blackListResponse = new BlackListResponse();
        HttpStatus httpStatus = HttpStatus.ACCEPTED;
        try {
            phoneNumberPayload.getPhoneNumbers().forEach(phoneNumber -> {

                // check for validation of the number
                if (PhoneNumberHelper.isValidPhoneNumber(phoneNumber) == Boolean.FALSE) {
                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, String.format("Phone Number %s is not valid", phoneNumber));
                }


                //Update in SQL DB
                PhoneNumber phoneNumber1 = new PhoneNumber();
                phoneNumber1.setPhoneNumber(phoneNumber);
                phoneNumber1.setStatus(PhoneNumberStatusEnum.WHITELISTED);
                phoneNumberService.updatePhoneNumber(phoneNumber1);

                //update in redis
                redisService.whiteListPhoneNumber(phoneNumber);

            });
            blackListResponse.setData("Successfully Whitelisted");
        } catch (HttpClientErrorException exception) {
            blackListResponse.setError(exception.getStatusText());
            httpStatus = exception.getStatusCode();
            log.info("Error while whitelisting numbers: {}, due to: {}", phoneNumberPayload, exception.getStatusText());
        } catch (Exception exception) {
            blackListResponse.setError(exception.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            log.info("Error while whitelisting numbers: {}, due to: {}", phoneNumberPayload, exception.getMessage());
        }
        log.info("WhiteListed all the numbers in: {}", phoneNumberPayload);
        return new ResponseEntity<>(blackListResponse, httpStatus);
    }

    /**
     * Takes no input params and returns the list of all the blacklisted phone numbers.
     */
    @GetMapping
    public ResponseEntity<BlackListResponse> getBlackListedNumbers() {

        BlackListResponse blackListResponse = new BlackListResponse();
        HttpStatus httpStatus = HttpStatus.ACCEPTED;
        try {
            // Get all the blacklisted number from the DB
            blackListResponse.setData(phoneNumberService.getAllBlockedNumbers());

        } catch (Exception exception) {
            blackListResponse.setError(exception.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(blackListResponse, httpStatus);
    }
}
