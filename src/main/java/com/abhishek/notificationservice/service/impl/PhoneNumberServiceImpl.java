package com.abhishek.notificationservice.service.impl;

import com.abhishek.notificationservice.model.entity.mysql.PhoneNumber;
import com.abhishek.notificationservice.repository.PhoneNumberRepository;
import com.abhishek.notificationservice.service.PhoneNumberService;
import com.abhishek.notificationservice.utils.enums.PhoneNumberStatusEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class PhoneNumberServiceImpl implements PhoneNumberService {
    private final PhoneNumberRepository phoneNumberRepository;

    public PhoneNumberServiceImpl(PhoneNumberRepository phoneNumberRepository) {
        this.phoneNumberRepository = phoneNumberRepository;
    }

    /**
     * Get PhoneNumber details by phoneNumber
     */
    @Override
    public PhoneNumber getPhoneNumber(String phoneNumber) {
        List<PhoneNumber> responseList = phoneNumberRepository.findByPhoneNumber(phoneNumber);
        if(responseList.size() == 0) return null;
        return responseList.get(0);
    }

    /**
     * Get details of all the blocked phone numbers
     */
    @Override
    public List<String> getAllBlockedNumbers() {
        List<String>  phoneNumbers =  new ArrayList<>();
        phoneNumberRepository.findByStatus(PhoneNumberStatusEnum.BLACKLISTED).forEach( phoneNumber -> { phoneNumbers.add(phoneNumber.getPhoneNumber()); });
        return phoneNumbers;
    }

    /**
     * Update the phoneNumber details in DB
     */
    @Override
    public void updatePhoneNumber(PhoneNumber phoneNumber) {
        PhoneNumber existingPhoneNumber = getPhoneNumber(phoneNumber.getPhoneNumber());
        if( existingPhoneNumber == null)
        {
            phoneNumberRepository.save(phoneNumber);
        } else {
            existingPhoneNumber.setPhoneNumber(phoneNumber.getPhoneNumber());
            existingPhoneNumber.setStatus(phoneNumber.getStatus());
            phoneNumberRepository.save(existingPhoneNumber);
        }

    }

    /**
     * Save the new phoneNumber details in DB
     */
    @Override
    public PhoneNumber savePhoneNumber(PhoneNumber phoneNumber) {
        return phoneNumberRepository.save(phoneNumber);
    }


}
