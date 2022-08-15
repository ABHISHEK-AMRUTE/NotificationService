package com.abhishek.notificationservice.kafka;

import com.abhishek.notificationservice.model.entity.mysql.SmsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private static  final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, Long> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Long> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage( Long requestId){
        LOGGER.info(String.format("Message sent %s", requestId));
        Message<Long> message1 = MessageBuilder.withPayload(requestId).setHeader(KafkaHeaders.TOPIC, "notification.send_sms").build();
        kafkaTemplate.send(message1);
    }
}
