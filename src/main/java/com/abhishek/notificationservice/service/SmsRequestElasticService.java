package com.abhishek.notificationservice.service;

import com.abhishek.notificationservice.model.entity.elasticSearch.SmsRequestElastic;

import java.util.Date;
import java.util.List;

public interface SmsRequestElasticService {
    void save(SmsRequestElastic smsRequestElastic);
    List<SmsRequestElastic> findById(String text, int pageNumber, int pageSize);
    List<SmsRequestElastic> findByPhoneNumber(String phoneNumber, Date dateFrom, Date dateTo);
}
