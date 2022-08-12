package com.abhishek.notificationservice.service.impl;

import com.abhishek.notificationservice.model.entity.elasticSearch.SmsRequestElastic;
import com.abhishek.notificationservice.service.SmsRequestElasticService;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public class SmsRequestElasticServiceImpl implements SmsRequestElasticService {
    private ElasticsearchRepository elasticsearchRepository;

    @Override
    public void save(SmsRequestElastic smsRequestElastic) {
        elasticsearchRepository.save(smsRequestElastic);
    }

    @Override
    public SmsRequestElastic findById(Long id) {
        return (SmsRequestElastic) elasticsearchRepository.findById(id).orElse(null);
    }

    public SmsRequestElasticServiceImpl(ElasticsearchRepository elasticsearchRepository) {
        this.elasticsearchRepository =  elasticsearchRepository;
    }

}
