package com.abhishek.notificationservice.service;

import com.abhishek.notificationservice.model.entity.elasticSearch.SmsRequestESDocument;

import java.util.Date;
import java.util.List;

public interface SmsRequestElasticService {
    void save(SmsRequestESDocument smsRequestESDocument);
    List<SmsRequestESDocument> findById(String text, int pageNumber, int pageSize);
    List<SmsRequestESDocument> findByPhoneNumber(String phoneNumber, Date dateFrom, Date dateTo, int page, int pageSize);
}
