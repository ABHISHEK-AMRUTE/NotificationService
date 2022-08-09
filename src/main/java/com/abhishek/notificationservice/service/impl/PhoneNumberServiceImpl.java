package com.abhishek.notificationservice.service.impl;

import com.abhishek.notificationservice.repository.PhoneNumberRepository;
import com.abhishek.notificationservice.service.PhoneNumberService;

public class PhoneNumberServiceImpl implements PhoneNumberService {
    private final PhoneNumberRepository phoneNumberRepository;

    public PhoneNumberServiceImpl(PhoneNumberRepository phoneNumberRepository) {
        this.phoneNumberRepository = phoneNumberRepository;
    }

}
