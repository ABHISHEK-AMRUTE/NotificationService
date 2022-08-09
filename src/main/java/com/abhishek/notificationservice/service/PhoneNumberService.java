package com.abhishek.notificationservice.service;

import com.abhishek.notificationservice.model.entity.mysql.PhoneNumber;

import java.util.List;

public interface PhoneNumberService {
    PhoneNumber getPhoneNumber(String phoneNumber);
    PhoneNumber savePhoneNumber(PhoneNumber phoneNumber);
    void updatePhoneNumber( PhoneNumber phoneNumber);
    List<String> getAllBlockedNumbers();
}
