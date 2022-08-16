package com.abhishek.notificationservice.controller;

import com.abhishek.notificationservice.kafka.KafkaProducer;
import com.abhishek.notificationservice.model.SmsDetailsResponse;
import com.abhishek.notificationservice.model.SmsErrorResponse;
import com.abhishek.notificationservice.model.SmsResponse;
import com.abhishek.notificationservice.model.SmsSuccessResponse;
import com.abhishek.notificationservice.model.entity.mysql.SmsRequest;
import com.abhishek.notificationservice.service.SmsRequestService;
import com.abhishek.notificationservice.utils.PhoneNumberHelper;
import com.abhishek.notificationservice.utils.enums.SmsStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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

    @PostMapping("/v1/sms/send")
    public ResponseEntity<SmsResponse> sendSms(@RequestBody SmsRequest smsRequest) {

        SmsResponse smsResponse = new SmsResponse();
        HttpStatus httpStatus = HttpStatus.ACCEPTED;

        try {
            if (smsRequest.getPhoneNumber() == null && smsRequest.getMessage() == null) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Phone number and message are mandatory");
            } else if (smsRequest.getPhoneNumber() == null) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Phone number is mandatory");
            } else if (smsRequest.getMessage() == null) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Message is mandatory");
            }
            if (PhoneNumberHelper.isValidPhoneNumber(smsRequest.getPhoneNumber()) == Boolean.FALSE) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Phone number is invalid");
            }
            smsRequest.setStatus(SmsStatusEnum.UNDER_PROCESSING);
            SmsRequest smsRequest1 = smsRequestService.saveSmsRequest(smsRequest);
            kafkaProducer.sendMessage(smsRequest1.getId());
            smsResponse.setData(new SmsSuccessResponse(String.valueOf(smsRequest1.getId()), "Successfully sent"));
        } catch (HttpClientErrorException exception) {
            SmsErrorResponse smsErrorResponse = new SmsErrorResponse(String.valueOf(exception.getStatusCode()), exception.getStatusText());
            smsResponse.setError(smsErrorResponse);
            httpStatus = exception.getStatusCode();
            log.info("Error while sending message: {}, due to : {}", smsRequest, exception.getStatusText() );
        } catch (Exception exception) {
            SmsErrorResponse smsErrorResponse = new SmsErrorResponse(String.valueOf(exception.hashCode()), exception.getMessage());
            smsResponse.setError(smsErrorResponse);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            log.info("Error while sending message: {}, due to : {}", smsRequest, exception.getMessage() );
        }

        log.info("Sent the sms for processing:{} ", smsRequest);
        return new ResponseEntity<>(smsResponse, httpStatus);
    }

    @GetMapping("/v1/sms/{id}")
    public ResponseEntity<SmsDetailsResponse> getSmsDetailsById(@PathVariable Long id) {
        SmsDetailsResponse smsDetailsResponse = new SmsDetailsResponse();
        HttpStatus httpStatus = HttpStatus.ACCEPTED;
        try {
            smsDetailsResponse.setData(smsRequestService.getSmsRequestById(id));
        } catch (NoSuchElementException exception) {
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
