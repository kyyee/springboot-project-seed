package com.kyyee.sps.configuration.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 第二个kafka客户端配置，用于多个kafka listener配置不通的场景
 */
@Configuration
@ConditionalOnProperty(prefix = "kyyee.secondary.kafka", name = "enabled")
@Slf4j
public class SecondaryKafkaAutoConfiguration {

    @Value("${spring.kafka.bootstrap-servers:kafka-hs:9093}")
    private String bootstrapServers;

    // 使用自定义的kafka配置，不依赖引用该包的项目的kafka配置，避免拉取消息间隔过小，小于30分钟，造成消息重复消费
    @Bean("secondaryKafkaFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> secondaryKafkaFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfigs()));
        factory.setBatchListener(false);
        factory.setConcurrency(1);
        return factory;
    }

    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        // Kafka session.timeout.ms heartbeat.interval.ms参数的区别以及对数据存储的一些思考
        // https://www.cnblogs.com/hapjin/archive/2019/06/01/10926882.html
        // 逻辑指标，设置为5分钟
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 5 * 60 * 1000);
        // 物理指标，设置为2分钟
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 2 * 60 * 1000);
        // 间隔30分钟
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 30 * 60 * 1000);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return props;
    }
}
