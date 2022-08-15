package com.abhishek.notificationservice.service.impl;

import com.abhishek.notificationservice.model.entity.elasticSearch.SmsRequestESDocument;
import com.abhishek.notificationservice.service.SmsRequestElasticService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import com.abhishek.notificationservice.repository.ElasticSearchRepository;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SmsRequestElasticServiceImpl implements SmsRequestElasticService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private ElasticSearchRepository elasticsearchRepository;

    private RestHighLevelClient restHighLevelClient;
    private ElasticsearchRestTemplate elasticsearchTemplate;
    public SmsRequestElasticServiceImpl(ElasticSearchRepository elasticsearchRepository, RestHighLevelClient restHighLevelClient, ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchRepository = elasticsearchRepository;
        this.restHighLevelClient = restHighLevelClient;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public void save(SmsRequestESDocument smsRequestESDocument) {
        elasticsearchRepository.save(smsRequestESDocument);
    }

    @Override
    public List<SmsRequestESDocument> findById(String text, int pageNumber, int pageSize) {
        List<SmsRequestESDocument> page = elasticsearchRepository.findByMessage(text, PageRequest.of(pageNumber,pageSize));
            return page;
    }

    @Override
    public List<SmsRequestESDocument> findByPhoneNumber(String phoneNumber, Date dateFrom, Date dateTo, int page, int pageSize) {
        return  elasticsearchRepository.findByPhoneNumberAndCreatedAtAfterAndCreatedAtBefore(phoneNumber, dateFrom, dateTo, PageRequest.of(page,pageSize));
    }


}
