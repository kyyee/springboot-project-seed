package com.kyyee.sps.configuration.kafka;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@ConfigurationProperties(prefix = "kyyee.kafka")
@Data
@Slf4j
public class KafkaTopicProperties {
    private List<Topic> topics;

    @Data
    public static class Topic {
        private String name;
        private Integer partitions;
        // 单机版kafka副本数
        private Integer singleReplicas;
        // 集群版kafka副本数
        private Integer clusterReplicas;

        public NewTopic of() {
            return of(null);
        }

        public NewTopic of(Integer nodeSize) {
            if (StringUtils.isEmpty(this.name)
                || ObjectUtils.isEmpty(this.partitions)
                || ObjectUtils.isEmpty(this.singleReplicas)
                || ObjectUtils.isEmpty(this.clusterReplicas)) {
                log.warn("kafka topic parameter is empty, name,partitions,singleReplicas,clusterReplicas is necessary.");
                return null;
            }
            TopicBuilder topicBuilder = TopicBuilder.name(this.name).partitions(this.partitions);
            if (!ObjectUtils.isEmpty(nodeSize) && nodeSize > 1) {
                // node的数量大于1，则为集群环境
                topicBuilder.replicas(this.clusterReplicas);
                log.info("node size:{}, cluster create topic", nodeSize);
            } else {
                // 单机环境，默认创建单机的topic
                topicBuilder.replicas(this.singleReplicas);
                log.info("node size:{}, single create topic", nodeSize);
            }
            NewTopic topic = topicBuilder.build();
            log.info("topic:{}", topic);
            return topic;
        }
    }
}
