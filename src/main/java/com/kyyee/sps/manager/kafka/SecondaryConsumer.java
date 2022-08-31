package com.kyyee.sps.manager.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "kyyee.secondary.kafka", name = "enabled")
@Slf4j
public class SecondaryConsumer {

    @KafkaListener(topics = {"${kyyee.kafka.topic[3].name:other_topic}"}, containerFactory = "secondaryKafkaFactory")
    public void listener(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            log.info("message:{}", record.value());
        } finally {
            acknowledgment.acknowledge();
        }
    }

}
