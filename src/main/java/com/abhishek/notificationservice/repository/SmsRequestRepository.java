package com.abhishek.notificationservice.repository;

import com.abhishek.notificationservice.model.entity.mysql.SmsRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsRequestRepository extends JpaRepository<SmsRequest, Long> {
}
