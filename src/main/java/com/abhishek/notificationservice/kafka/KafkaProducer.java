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
    private KafkaTemplate<String, SmsRequest> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, SmsRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage( SmsRequest message){
        LOGGER.info(String.format("Message sent %s", message));
        Message<SmsRequest> message1 = MessageBuilder.withPayload(message).setHeader(KafkaHeaders.TOPIC, "smsRequest").build();
        kafkaTemplate.send(message1);
    }
}
