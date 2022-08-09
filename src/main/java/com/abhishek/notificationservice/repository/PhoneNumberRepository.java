package com.abhishek.notificationservice.repository;

import com.abhishek.notificationservice.model.entity.mysql.PhoneNumber;
import com.abhishek.notificationservice.util.enums.PhoneNumberStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhoneNumberRepository extends JpaRepository<PhoneNumber,String> {
    List<PhoneNumber> findByPhoneNumber(String phoneNumber);
    List<PhoneNumber> findByStatus(PhoneNumberStatusEnum phoneNumberStatusEnum);
}
