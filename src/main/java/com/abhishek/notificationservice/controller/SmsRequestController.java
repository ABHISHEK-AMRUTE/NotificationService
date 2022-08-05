package com.abhishek.notificationservice.controller;

import com.abhishek.notificationservice.model.entity.mysql.SmsRequest;
import com.abhishek.notificationservice.service.SmsRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class SmsRequestController {

    private SmsRequestService smsRequestService;

    public SmsRequestController(SmsRequestService smsRequestService) {
        super();
        this.smsRequestService = smsRequestService;
    }

    @PostMapping("/v1/sms/send")
    public ResponseEntity<String> sendSms(@RequestBody SmsRequest smsRequest ){
        System.out.println(smsRequest);
        return smsRequestService.sendSms(smsRequest);
    }
}
