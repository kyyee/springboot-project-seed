package com.kyyee.sps.controller.mock;

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
@RequestMapping("${api-prefix}/mocks")
@Slf4j
@Tag(name = "kafka消息模拟服务")
public class KafkaMockController {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private KafkaTopicProperties topicProperties;

    private static final Map<String, Future<?>> FUTURE_MAP = new ConcurrentHashMap<>();

    private final ScheduledExecutorService service = new ScheduledThreadPoolExecutor(3);

    @GetMapping("/kafka-send")
    @Operation(summary = "模拟接入方发送一条kafka消息")
    public String sendNotification(@RequestParam(value = "count", defaultValue = "1") Integer count,
                                   @RequestParam(value = "type", defaultValue = "1") Short type,
                                   @RequestParam(value = "ext_type", defaultValue = "1") Short extType,
                                   @RequestParam(value = "popup_type", required = false) Short popupType,
                                   @RequestParam(value = "title", defaultValue = "这是一条消息的标题") String title,
                                   @RequestParam(value = "content", required = false, defaultValue = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. "
                                       + "Animi assumenda cumque, deleniti eius eum hic, nisi perspiciatis placeat praesentium quas reprehenderit sed?"
                                       + " Ab cum doloremque fugit optio reprehenderit! Aspernatur, doloribus.") String content,
                                   @RequestParam(value = "callback_url", defaultValue = "/home") String callbackUrl,
                                   @RequestParam(value = "receiver", required = false) String receiver,
                                   @RequestParam(value = "receive_type", defaultValue = "0") Short receiveType) {
        return null;
    }

    @GetMapping("/start")
    @Operation(summary = "开始模拟发送随机消息（每百毫秒）")
    public String mockStart(@RequestParam(value = "period", defaultValue = "10") Integer period) {
        final String threadId = UUID.randomUUID().toString();
        Future<?> future = service.scheduleAtFixedRate(() -> {
            // sendNotification(1);
        }, 0L, new Random().nextInt(period * 100), TimeUnit.MILLISECONDS);

        log.info("the thread id is:{}, period is:{}", threadId, period);
        FUTURE_MAP.put(threadId, future);

        return threadId;
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
