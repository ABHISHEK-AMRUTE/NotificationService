package com.abhishek.notificationservice.repository;

import com.abhishek.notificationservice.model.entity.elasticSearch.SmsRequestElastic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Date;
import java.util.List;

public interface ElasticSearchRepository extends ElasticsearchRepository<SmsRequestElastic , String> {
    List<SmsRequestElastic> findByMessage(String message, Pageable page);
    List<SmsRequestElastic> findByPhoneNumberAndCreatedAtAfterAndCreatedAtBefore(String phoneNumber, Date dateFrom, Date dateTill, Pageable pageable);
}
