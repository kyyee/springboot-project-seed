package com.kyyee.sps.common.component.scheduler;

import com.kyyee.sps.configuration.kafka.KafkaInitialConfiguration;
import com.kyyee.sps.configuration.kafka.KafkaTopicProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * kafka 定时检测消费组是否在线，下线的重新拉起
 */
@Component
@EnableConfigurationProperties(KafkaTopicProperties.class)
@AutoConfigureAfter(KafkaInitialConfiguration.class)
@Slf4j
public class KafkaConsumerRestartTask {

    public static final int CONNECTIONS_MAX_IDLE_MS_CONFIG = 10000;
    public static final int REQUEST_TIMEOUT_MS_CONFIG = 5000;

    @Resource
    private KafkaAdmin kafkaAdmin;
    @Resource
    private KafkaTopicProperties topicProperties;
    @Resource
    private KafkaListenerEndpointRegistry endpointRegistry;
    List<String> topics;

    /**
     * 计划任务，每隔5分钟执行一次
     */
    @Scheduled(cron = "${kyyee.config.kafka.container.restart-corn:0 0/5 * * * ?}")
    public void consumerRestart() {
        Instant start = Instant.now();
        doRestart();
        log.info("the task used:{}s", ChronoUnit.SECONDS.between(start, Instant.now()));
    }

    public void doRestart() {
        if (CollectionUtils.isEmpty(this.topics)) {
            this.topics = topicProperties.getTopics().stream().map(KafkaTopicProperties.Topic::getName).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(this.topics)) {
                return;
            }
        }
        // kafka服务端配置信息
        Map<String, Object> properties = new HashMap<>(kafkaAdmin.getConfigurationProperties());
        properties.put(AdminClientConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, CONNECTIONS_MAX_IDLE_MS_CONFIG);
        properties.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, REQUEST_TIMEOUT_MS_CONFIG);

        // 创建KafkaAdminClient
        try (AdminClient client = KafkaAdminClient.create(properties)) {

            // 获取在线消费者列表
            List<String> groups = Collections.singletonList(String.valueOf(properties.get("spring.kafka.consumer.group-id")));
            // 获取在线消费者列表订阅的topic集合
            Set<String> assignedTopics = client.describeConsumerGroups(groups).all().get().values()
                .stream().flatMap(consumerGroupDescription -> consumerGroupDescription.members().stream())
                .flatMap(memberDescription -> memberDescription.assignment().topicPartitions().stream().map(TopicPartition::topic))
                .collect(Collectors.toSet());

            //kafka 集群当前的所有topic
            Set<String> allClusterTopics = client.listTopics().names().get();

            // 过滤获得未订阅的topic集合（消费者离线）
            List<String> unassignedTopics = this.topics.stream().filter(e -> !assignedTopics.contains(e) && allClusterTopics.contains(e)).collect(Collectors.toList());

            if (unassignedTopics.isEmpty()) {
                log.info("unassigned topics is empty.");
                return;
            }
            log.info("unassigned topics:{}", unassignedTopics);

            //获取监听了未订阅topic的kafka监听器
            List<MessageListenerContainer> needRestartContainers = new LinkedList<>();
            Collection<MessageListenerContainer> allListenerContainers = endpointRegistry.getAllListenerContainers();
            for (MessageListenerContainer listenerContainer : allListenerContainers) {
                ContainerProperties containerProperties = listenerContainer.getContainerProperties();
                for (String topic : unassignedTopics) {
                    boolean topicCheck = Optional.ofNullable(containerProperties.getTopics()).map(Arrays::asList).map(list -> list.contains(topic)).orElse(false);
                    boolean topicPatternCheck = Optional.ofNullable(containerProperties.getTopicPattern()).map(pattern -> pattern.matcher(topic).find()).orElse(false);
                    if (topicCheck || topicPatternCheck) {
                        needRestartContainers.add(listenerContainer);
                    }
                }
            }
            if (needRestartContainers.isEmpty()) {
                log.info("need restart containers is empty.");
                return;
            }
            //依次重启kafka监听器
            for (MessageListenerContainer toRestartContainer : needRestartContainers) {
                AbstractMessageListenerContainer container = (AbstractMessageListenerContainer) toRestartContainer;
                log.info("kafka consumer restart, container:{}", container.getContainerProperties());
                container.stop(false);
                container.start();
            }
        } catch (Exception e) {
            log.error("kafka consumer restart failed, message:{}", e.getMessage());
        }
    }

}
