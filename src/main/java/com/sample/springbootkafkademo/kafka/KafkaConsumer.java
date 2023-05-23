package com.sample.springbootkafkademo.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    int counter = 1;

    @KafkaListener(topics = "firstTopic", groupId = "myGroup")
    public void consumer(String message) {
        LOGGER.info(String.format("This is Kafka Consumer : " + counter + " message received -> %s", message));
        counter++;
    }
}
