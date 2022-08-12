package com.abhishek.notificationservice.service;

import com.abhishek.notificationservice.model.entity.elasticSearch.SmsRequestElastic;

public interface SmsRequestElasticService {
    void save(SmsRequestElastic smsRequestElastic);
    SmsRequestElastic findById(Long id);
}
