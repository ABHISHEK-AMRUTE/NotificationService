package com.abhishek.notificationservice.controller;

import com.abhishek.notificationservice.model.ESSmsRequestResponse;
import com.abhishek.notificationservice.model.SmsErrorResponse;
import com.abhishek.notificationservice.model.entity.elasticSearch.SmsRequestESDocument;
import com.abhishek.notificationservice.service.impl.SmsRequestElasticServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
public class SmsRequestESController {
    private SmsRequestElasticServiceImpl smsRequestElasticService;

    public SmsRequestESController(SmsRequestElasticServiceImpl smsRequestElasticService) {
        this.smsRequestElasticService = smsRequestElasticService;
    }

    @GetMapping("/v1/smsRequest")
    public ResponseEntity<ESSmsRequestResponse> getSmsRequestsByText(@RequestParam String textQuery, @RequestParam int page, @RequestParam int pageSize) {
        ESSmsRequestResponse esSmsRequestResponse = new ESSmsRequestResponse();
        HttpStatus httpStatus = HttpStatus.ACCEPTED;
        try {
            esSmsRequestResponse.setData(smsRequestElasticService.findById(textQuery, page, pageSize));
        } catch (Exception exception) {
            SmsErrorResponse smsErrorResponse = new SmsErrorResponse(String.valueOf(exception.hashCode()), exception.getMessage());
            esSmsRequestResponse.setError(smsErrorResponse);
        }
        return new ResponseEntity<>(esSmsRequestResponse, httpStatus);
    }

    @GetMapping("/v1/smsRequest/phoneNumber")
    public ResponseEntity<ESSmsRequestResponse> getSmsRequestByPhoneNumber(@RequestParam String phoneNumber, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFrom, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo, @RequestParam int page, @RequestParam int pageSize) {
        ESSmsRequestResponse esSmsRequestResponse = new ESSmsRequestResponse();
        HttpStatus httpStatus = HttpStatus.ACCEPTED;
        try {
            esSmsRequestResponse.setData(smsRequestElasticService.findByPhoneNumber(phoneNumber, dateFrom, dateTo, page, pageSize));
        } catch (Exception exception) {
            SmsErrorResponse smsErrorResponse = new SmsErrorResponse(String.valueOf(exception.hashCode()), exception.getMessage());
            esSmsRequestResponse.setError(smsErrorResponse);
        }
        return new ResponseEntity<>(esSmsRequestResponse, httpStatus);
    }

}
