package com.kyyee.sps.manager.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyyee.sps.common.component.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
public class WebSocketSender implements InitializingBean {

    @Value("${kyyee.websocket.notification-channel.send-notification-interval:200}")
    private Integer sendNotificationInterval;

    @Value("${kyyee.websocket.notification-channel.name:notification-channel}")
    private String notificationChannelName;

    @Value("${kyyee.websocket.notification-exception-channel.name:notification-exception-channel}")
    private String notificationExceptionChannelName;

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    @Resource
    private ObjectMapper objectMapper;

    public void sendNotification(Notification notification) {
        if (ObjectUtils.isEmpty(UserCache.getAll())) {
            return;
        }

        UserCache.getAll().forEach((clientId, user) -> {
            if (user.getUserCode().equals(notification.getReceiver()) &&
                // 对推送到同一客户端的消息进行限流，同一客户端默认1秒最多推送5次消息
                ChronoUnit.MILLIS.between(user.getLastSendTime(), Instant.now()) >= sendNotificationInterval) {

                // 外网为1，内网为0
                Integer outer = 1;
                if (outer.equals(user.getOuter())) {
                    // 外网设置
                    log.debug("outer");
                } else {
                    // 内网设置
                    log.debug("inner");
                }

                String message;
                try {
                    message = objectMapper.writeValueAsString(notification);
                } catch (JsonProcessingException e) {
                    log.error("serialize notification_channel websocket message error. message:{}", e.getMessage(), e);
                    return;
                }
                // 点对点消息会发送到 [/user/{clientId}/{notificationChannelName}]
                simpMessagingTemplate.convertAndSendToUser(clientId, notificationChannelName, message);
                // 更新最后推送时间
                user.setLastSendTime(Instant.now());
                log.debug("websocket push notification_channel message success, client_id={}, usercode={}, send message:{}", clientId, user.getUserCode(), message);
            }
        });
    }

    @MessageExceptionHandler(Exception.class)
    public void exception(Exception e) {
        log.error("exception={}", e.getMessage(), e);

        String clientId = "";
        // 点对点失败的消息会发送到 [/user/{clientId}/{notificationExceptionChannelName}]
        simpMessagingTemplate.convertAndSendToUser(clientId, notificationExceptionChannelName, e.getMessage());

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("kyyee.websocket.notification-channel.send-notification-interval is:{}", sendNotificationInterval);
    }
}
