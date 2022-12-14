package com.abhishek.notificationservice.service;

import com.abhishek.notificationservice.model.entity.mysql.SmsRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface SmsRequestService {
    SmsRequest saveSmsRequest(SmsRequest smsRequest);
    SmsRequest getSmsRequestById( Long id );
}
