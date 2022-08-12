package com.abhishek.notificationservice.repository;

import com.abhishek.notificationservice.model.entity.elasticSearch.SmsRequestElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticSearchRepository extends ElasticsearchRepository<SmsRequestElastic , Long> {
}
