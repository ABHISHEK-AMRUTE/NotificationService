package com.abhishek.notificationservice.service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducer {
    private final KafkaTemplate<String, Long> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Long> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage( Long requestId ){
        log.info("Message send: {}", requestId);
        Message<Long> message1 = MessageBuilder.withPayload(requestId).setHeader(KafkaHeaders.TOPIC, "notification.send_sms").build();
        kafkaTemplate.send(message1);
    }
}
