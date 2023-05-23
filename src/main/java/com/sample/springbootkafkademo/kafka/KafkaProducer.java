package com.sample.springbootkafkademo.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class KafkaProducer {
    private KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);
    int counter = 1;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(@RequestParam String message) {
        kafkaTemplate.send("firstTopic", "Msg",message);
        LOGGER.info(String.format("This is Kafka Producer: Message number " + counter + " in first topic is %s", message));
        counter++;
    }
}