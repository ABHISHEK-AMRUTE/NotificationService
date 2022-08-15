package com.abhishek.notificationservice.repository;

import com.abhishek.notificationservice.model.entity.elasticSearch.SmsRequestESDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Date;
import java.util.List;

public interface ElasticSearchRepository extends ElasticsearchRepository<SmsRequestESDocument, String> {
    List<SmsRequestESDocument> findByMessage(String message, Pageable page);
    List<SmsRequestESDocument> findByPhoneNumberAndCreatedAtAfterAndCreatedAtBefore(String phoneNumber, Date dateFrom, Date dateTill, Pageable pageable);
}
