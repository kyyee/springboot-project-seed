package com.kyyee.sps.controller.mock;

import com.kyyee.framework.common.base.Res;
import com.kyyee.sps.configuration.kafka.KafkaTopicProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;

@RestController
@ConditionalOnProperty(prefix = "kyyee.kafka", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(KafkaTopicProperties.class)
@RequestMapping("${api-prefix}/mocks/kafka")
@Slf4j
@Tag(name = "kafka消息模拟服务")
public class KafkaMockController {

    private static final Map<String, Future<?>> FUTURE_MAP = new ConcurrentHashMap<>();

    private final ScheduledExecutorService service = new ScheduledThreadPoolExecutor(3);

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private KafkaTopicProperties topicProperties;

    @GetMapping("/kafka-send")
    @Operation(summary = "模拟接入方发送一条kafka消息")
    public Object sendNotification(@RequestParam(value = "count", defaultValue = "1") Integer count) {
        return null;
    }

    @GetMapping("/start")
    @Operation(summary = "开始模拟发送随机消息（每百毫秒）")
    public Res<String> mockStart(@RequestParam(value = "period", defaultValue = "10") Integer period) {
        final String threadId = UUID.randomUUID().toString();
        Future<?> future = service.scheduleAtFixedRate(() -> sendNotification(1), 0L, new Random().nextInt(period * 100), TimeUnit.MILLISECONDS);

        log.info("the thread id is:{}, period is:{}", threadId, period);
        FUTURE_MAP.put(threadId, future);

        return Res.success(threadId);
    }

    @DeleteMapping("/stop/{thread_id}")
    @Operation(summary = "停止模拟某随机消息任务")
    public void mockStop(@PathVariable(value = "thread_id") String threadId) {
        Future<?> future = FUTURE_MAP.get(threadId);
        if (!ObjectUtils.isEmpty(future) && !future.isCancelled()) {
            future.cancel(true);
        }
        FUTURE_MAP.remove(threadId);
    }

    @DeleteMapping("/stop")
    @Operation(summary = "停止模拟所有随机消息任务")
    public void mockStop() {
        FUTURE_MAP.values().forEach(future -> {
            if (!ObjectUtils.isEmpty(future) && !future.isCancelled()) {
                future.cancel(true);
            }
        });
        FUTURE_MAP.clear();
    }
}
