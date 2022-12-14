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
    public SmsRequest getSmsRequestById(Long id) {
        return smsRequestRepository.findById(id).get();
    }

    @Override
    public SmsRequest saveSmsRequest(SmsRequest smsRequest) {
         return smsRequestRepository.save(smsRequest);
    }
}
