package com.abhishek.notificationservice.repository;

import com.abhishek.notificationservice.model.entity.mysql.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneNumberRepository extends JpaRepository<PhoneNumber,String> {
}
