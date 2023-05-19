package com.sample.springbootkafkademo.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);
    int counter = 1;

    @KafkaListener(topics = "firstTopic", groupId = "myGroup")
    public void consumer(String message) {
        LOGGER.info(String.format("Message number " + counter + " received -> %s", message));
        counter++;
    }
}
