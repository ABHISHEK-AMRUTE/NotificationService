package com.abhishek.notificationservice.kafka;

import com.abhishek.notificationservice.model.entity.mysql.SmsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private  static  final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "smsRequest", groupId = "myGroup")
    public void consume(SmsRequest message){
        LOGGER.info(String.format("Message recieved %s", message));
    }
}
