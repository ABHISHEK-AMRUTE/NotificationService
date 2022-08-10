package com.abhishek.notificationservice.service.impl;

import com.abhishek.notificationservice.repository.RedisRepository;
import com.abhishek.notificationservice.service.RedisService;
import com.abhishek.notificationservice.utils.enums.PhoneNumberStatusEnum;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService {
    private RedisRepository redisRepository;

    public RedisServiceImpl(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    public void savePhoneNumber(String phoneNumber) {
        redisRepository.savePhoneNumber(phoneNumber);
    }

    @Override
    public void blackListPhoneNumber(String phoneNumber) {
        redisRepository.blackListPhoneNumber(phoneNumber);
    }

    @Override
    public void whiteListPhoneNumber(String phoneNumber) {
        redisRepository.whiteListPhoneNumber(phoneNumber);
    }

    @Override
    public PhoneNumberStatusEnum getPhoneNumberStatus(String phoneNumber) {
        return redisRepository.getPhoneNumberStatus(phoneNumber);
    }
}
