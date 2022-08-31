package com.kyyee.sps.manager.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrimaryConsumer {

    @KafkaListener(topics = {"${kyyee.kafka.topic[0].name:user_insert_event}"})
    public void listener(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            log.info("message:{}", record.value());
        } finally {
            acknowledgment.acknowledge();
        }
    }
}
