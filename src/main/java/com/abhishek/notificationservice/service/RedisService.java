package com.abhishek.notificationservice.service;

import com.abhishek.notificationservice.utils.enums.PhoneNumberStatusEnum;

public interface RedisService {
    void savePhoneNumber(String phoneNumber);
    void blackListPhoneNumber( String phoneNumber);
    void whiteListPhoneNumber( String phoneNumber);
    PhoneNumberStatusEnum getPhoneNumberStatus( String phoneNumber );
}
