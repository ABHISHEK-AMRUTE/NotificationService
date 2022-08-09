package com.abhishek.notificationservice.service.impl;

import com.abhishek.notificationservice.model.entity.mysql.SmsRequest;
import com.abhishek.notificationservice.repository.SmsRequestRepository;
import com.abhishek.notificationservice.service.SmsRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SmsRequestServiceImpl implements SmsRequestService {

    private final SmsRequestRepository smsRequestRepository;

    public SmsRequestServiceImpl(SmsRequestRepository smsRequestRepository) {
        super();
        this.smsRequestRepository = smsRequestRepository;
    }

    @Override
    public ResponseEntity<String> sendSms(SmsRequest smsRequest) {

        smsRequestRepository.save(smsRequest);
        return new ResponseEntity<String>("sample", HttpStatus.ACCEPTED);
    }
}
