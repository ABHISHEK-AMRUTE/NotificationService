package com.abhishek.notificationservice.service.impl;

import com.abhishek.notificationservice.model.entity.elasticSearch.SmsRequestElastic;
import com.abhishek.notificationservice.service.SmsRequestElasticService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import com.abhishek.notificationservice.repository.ElasticSearchRepository;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
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
    public void save(SmsRequestElastic smsRequestElastic) {
        elasticsearchRepository.save(smsRequestElastic);
    }

    @Override
    public List<SmsRequestElastic> findById(String text, int pageNumber, int pageSize) {
        List<SmsRequestElastic> page = elasticsearchRepository.findByMessage(text, PageRequest.of(pageNumber,pageSize));
            return page;
    }

    @Override
    public List<SmsRequestElastic> findByPhoneNumber(String phoneNumber, Date dateFrom, Date dateTo) {
        return  elasticsearchRepository.findByPhoneNumberAndCreatedAtAfterAndCreatedAtBefore(phoneNumber, dateFrom, dateTo, PageRequest.of(0,10));
//        return elasticsearchRepository.findByPhoneNumber(phoneNumber, PageRequest.of(0,10));
    }

//    public List<SmsRequestElastic> getByTextquery(String textQuery){
//        elasticsearchTemplate.search()
//    }

}
