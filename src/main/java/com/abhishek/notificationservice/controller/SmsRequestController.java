package com.abhishek.notificationservice.controller;

import com.abhishek.notificationservice.service.kafka.KafkaProducer;
import com.abhishek.notificationservice.model.SmsDetailsResponse;
import com.abhishek.notificationservice.model.SmsErrorResponse;
import com.abhishek.notificationservice.model.SmsResponse;
import com.abhishek.notificationservice.model.SmsSuccessResponse;
import com.abhishek.notificationservice.model.entity.mysql.SmsRequest;
import com.abhishek.notificationservice.service.SmsRequestService;
import com.abhishek.notificationservice.utils.PhoneNumberHelper;
import com.abhishek.notificationservice.utils.enums.SmsStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.NoSuchElementException;

@Slf4j
@Controller
public class SmsRequestController {

    private SmsRequestService smsRequestService;
    private KafkaProducer kafkaProducer;


    public SmsRequestController(SmsRequestService smsRequestService, KafkaProducer kafkaProducer) {
        this.smsRequestService = smsRequestService;
        this.kafkaProducer = kafkaProducer;
    }

    /**
     * @param smsRequest ( phoneNumber and message )
     * @return the appropriate response in accordance with the status of the smsRequest
     * Step 1: Check the validation of input params, validate the phone number.
     * Step 2: Save SmsRequest with status as under_processing
     * Step 3: Produce a message to the notification Consumer
     */
    @PostMapping("/v1/sms/send")
    public ResponseEntity<SmsResponse> sendSms(@RequestBody SmsRequest smsRequest) {

        SmsResponse smsResponse = new SmsResponse();
        HttpStatus httpStatus = HttpStatus.ACCEPTED;

        try {
            // Validate the Input parameters
            if (smsRequest.getPhoneNumber() == null && smsRequest.getMessage() == null) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Phone number and message are mandatory");
            } else if (smsRequest.getPhoneNumber() == null) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Phone number is mandatory");
            } else if (smsRequest.getMessage() == null) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Message is mandatory");
            }

            // Validate the phone Number
            if (PhoneNumberHelper.isValidPhoneNumber(smsRequest.getPhoneNumber()) == Boolean.FALSE) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Phone number is invalid");
            }

            //Update the status of the smsRequest
            smsRequest.setStatus(SmsStatusEnum.UNDER_PROCESSING);
            SmsRequest smsRequest1 = smsRequestService.saveSmsRequest(smsRequest);

            // Produce the message to the notification consumer
            kafkaProducer.sendMessage(smsRequest1.getId());

            // Send the requestId and comment in response
            smsResponse.setData(new SmsSuccessResponse(String.valueOf(smsRequest1.getId()), "Successfully sent"));

        } catch (HttpClientErrorException exception) {
            // Handling the client side error exception.
            SmsErrorResponse smsErrorResponse = new SmsErrorResponse(String.valueOf(exception.getStatusCode()), exception.getStatusText());
            smsResponse.setError(smsErrorResponse);
            httpStatus = exception.getStatusCode();
            log.info("Error while sending message: {}, due to : {}", smsRequest, exception.getStatusText());
        } catch (Exception exception) {
            SmsErrorResponse smsErrorResponse = new SmsErrorResponse(String.valueOf(exception.hashCode()), exception.getMessage());
            smsResponse.setError(smsErrorResponse);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            log.info("Error while sending message: {}, due to : {}", smsRequest, exception.getMessage());
        }

        log.info("Sent the sms for processing:{} ", smsRequest);
        return new ResponseEntity<>(smsResponse, httpStatus);
    }

    /**
     * @param id , this is the requestId of the smsRequest that has to be tracked
     * @return the smsRequest Details.
     */
    @GetMapping("/v1/sms/{id}")
    public ResponseEntity<SmsDetailsResponse> getSmsDetailsById(@PathVariable Long id) {
        SmsDetailsResponse smsDetailsResponse = new SmsDetailsResponse();
        HttpStatus httpStatus = HttpStatus.ACCEPTED;
        try {
            smsDetailsResponse.setData(smsRequestService.getSmsRequestById(id));
        } catch (NoSuchElementException exception) {

            // Handling the resource not found exception
            SmsErrorResponse smsErrorResponse = new SmsErrorResponse("INVALID_REQUEST", "request_id not found");
            smsDetailsResponse.setError(smsErrorResponse);
            httpStatus = HttpStatus.NOT_FOUND;

        } catch (Exception exception) {
            SmsErrorResponse smsErrorResponse = new SmsErrorResponse(String.valueOf(exception.hashCode()), exception.getMessage());
            smsDetailsResponse.setError(smsErrorResponse);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(smsDetailsResponse, httpStatus);
    }

}
