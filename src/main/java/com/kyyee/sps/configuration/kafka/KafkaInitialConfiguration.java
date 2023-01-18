package com.kyyee.sps.configuration.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.web.context.support.GenericWebApplicationContext;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnProperty(prefix = "kyyee.kafka", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(KafkaTopicProperties.class)
@Import(KafkaAutoConfiguration.class)
@Slf4j
public class KafkaInitialConfiguration implements InitializingBean {

    public static final int CONNECTIONS_MAX_IDLE_MS_CONFIG = 10000;
    public static final int REQUEST_TIMEOUT_MS_CONFIG = 5000;
    @Resource
    private KafkaAdmin kafkaAdmin;

    @Resource
    private KafkaTopicProperties topicProperties;

    @Resource
    private GenericWebApplicationContext applicationContext;

    public void createTopics() throws ExecutionException, InterruptedException {
        Map<String, Object> properties = new HashMap<>(kafkaAdmin.getConfigurationProperties());
        properties.put(AdminClientConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, CONNECTIONS_MAX_IDLE_MS_CONFIG);
        properties.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, REQUEST_TIMEOUT_MS_CONFIG);


        try (AdminClient client = KafkaAdminClient.create(properties)) {
            ListTopicsResult topics = client.listTopics();
            Set<String> topicNames = topics.names().get();
            List<KafkaTopicProperties.Topic> newTopics = topicProperties.getTopics();
            Integer nodeSize = client.describeCluster().nodes().get(REQUEST_TIMEOUT_MS_CONFIG, TimeUnit.MILLISECONDS).size();

            for (KafkaTopicProperties.Topic newTopic : newTopics) {
                if (!topicNames.isEmpty() && topicNames.contains(newTopic.getName())) {
                    log.info("kafka topic: {} is exist. kafka client will modify it.", newTopic);
                }
                applicationContext.registerBean(newTopic.getName(), NewTopic.class, () -> newTopic.of(nodeSize));
            }
        } catch (Exception e) {
            // kafka is not available
            log.error("create kafka topic failed, message:{}", e.getMessage());
            // 如果失败，则不再创建topic，初始化失败并退出启动
            System.exit(-1);
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        createTopics();
    }
}
