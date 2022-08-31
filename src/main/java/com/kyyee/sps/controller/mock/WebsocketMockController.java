package com.kyyee.sps.controller.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyyee.framework.common.base.Res;
import com.kyyee.framework.common.utils.ThreadUtils;
import com.kyyee.sps.common.component.cache.UserCache;
import com.kyyee.sps.common.utils.BeanCopyUtils;
import com.kyyee.sps.dto.websocket.SeedMessage;
import com.kyyee.sps.manager.websocket.Notification;
import com.kyyee.sps.manager.websocket.WebSocketSender;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.MimeType;
import org.springframework.util.ObjectUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("${api-prefix}/mocks")
@Slf4j
@Tag(name = "消息组件websocket消息服务")
public class WebsocketMockController {

    private static final Map<String, StompSession> SESSION_MAP = new ConcurrentHashMap<>();

    @Resource
    private ObjectMapper objectMapper;

    @Value("${server.port:80}")
    private String serverPort;

    @Value("${kyyee.websocket.base-path:NA}")
    private String endpoint;

    @Resource
    private WebSocketSender webSocketSender;

    /**
     * 消息组件消息种子，消息只向已经 登录的用户 发送告警
     */
    @MessageMapping("${kyyee.websocket.seed-channel:NA}")
    @Operation(summary = "消息组件消息种子")
    public void subscribe(SeedMessage seed) {
        log.debug("websocket seed-channel: {}", seed);
        UserCache.User cacheUser = BeanCopyUtils.convert(seed.getUser(), UserCache.User.class);
        UserCache.set(seed.getId(), cacheUser);
    }

    @GetMapping("/websocket-connect")
    @Operation(summary = "模拟websocket客户端")
    public Res<String> websocketConnect(@RequestParam(value = "user_code", required = false, defaultValue = "admin") String userCode,
                                        @RequestParam(value = "user_name", required = false, defaultValue = "admin") String userName) {
        // 建立连接
        log.info("websocket connect start...");
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());
        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new StringMessageConverter());
        stompClient.setReceiptTimeLimit(300);
        stompClient.setDefaultHeartbeat(new long[]{10000L, 10000L});
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.afterPropertiesSet();
        stompClient.setTaskScheduler(taskScheduler);

        WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setContentType(MimeType.valueOf("application/json"));
        final String clientId = UUID.randomUUID().toString();
        stompHeaders.setId(clientId);
        try {
            stompHeaders.set("user", URLEncoder.encode("usercode:" + userCode + "&" + "username:" + userName, StandardCharsets.UTF_8.displayName()));
        } catch (UnsupportedEncodingException e) {
            // The system should always have the platform default
        }

        stompHeaders.set("outer", String.valueOf(0));

        StompSessionHandler receiveTextStompSessionHandler = new StompSessionHandlerAdapter() {

            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                super.afterConnected(session, connectedHeaders);
                log.info("after connected");
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers,
                                        byte[] payload, Throwable exception) {
                super.handleException(session, command, headers, payload, exception);
                log.error("connect or subscribe failed. message:{}", exception.getMessage(), exception);
                ThreadUtils.sleep(Duration.ofMinutes(1L).toMillis());
                websocketConnect(userCode, userName);
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                super.handleFrame(headers, payload);
                log.info("the subscription message is:{}", payload);
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                super.handleTransportError(session, exception);
                log.error("connect or subscribe failed. message:{}", exception.getMessage(), exception);
                ThreadUtils.sleep(Duration.ofMinutes(1L).toMillis());
                websocketConnect(userCode, userName);
            }
        };

        ListenableFuture<StompSession> future = stompClient.connect(
            "ws://localhost:" + serverPort + endpoint, httpHeaders, stompHeaders,
            receiveTextStompSessionHandler);

        try {
            // 定义全局变量，代表一个session
            StompSession stompSession = future.get();
            stompSession.setAutoReceipt(true);
            stompSession.subscribe("/user/" + clientId + "/notification-channel", receiveTextStompSessionHandler);

            SESSION_MAP.put(clientId, stompSession);

            // stompSession.send("/app/api/mg/v2/msg-center/websocket/notifications-subscribe", "test");
        } catch (InterruptedException e) {
            log.error("websocket connect failed, message:{}", e.getMessage(), e);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            log.error("websocket connect failed, message:{}", e.getMessage(), e);
        }
        return Res.success(clientId);
    }

    @DeleteMapping("/websocket-disconnect/{client_id}")
    @Operation(summary = "断开websocket客户端")
    public void websocketDisconnect(@PathVariable(value = "client_id") String clientId) {
        StompSession stompSession = SESSION_MAP.get(clientId);
        if (!ObjectUtils.isEmpty(stompSession) && stompSession.isConnected()) {
            stompSession.disconnect();
        }
        SESSION_MAP.remove(clientId);
    }

    @DeleteMapping("/websocket-disconnect")
    @Operation(summary = "断开所有websocket客户端")
    public void websocketDisconnect() {
        SESSION_MAP.values().forEach(stompSession -> {
            if (!ObjectUtils.isEmpty(stompSession) && stompSession.isConnected()) {
                stompSession.disconnect();
            }
        });
        SESSION_MAP.clear();
    }

    @SneakyThrows
    @GetMapping("/websocket-send")
    @Operation(summary = "模拟发送一条websocket消息")
    public void websocketSend(@RequestParam(value = "user_code", required = false, defaultValue = "admin") String userCode,
                              @RequestParam(value = "user_name", required = false, defaultValue = "admin") String userName) {

        // exception message:
        // java.io.FileNotFoundException: class path resource [mocks/person.json] cannot be resolved to absolute file path
        // because it does not reside in the file system:
        // jar:file:/E:/GitlabIdeaProjects/message-center/target/app.jar!/BOOT-INF/classes!/mocks/person.json
        // 为什么使用new ClassPathResource("mocks/person.json").getInputStream()而不是new ClassPathResource("mocks/person.json").getFile()，如下Q&A
        // @link <a href='https://stackoverflow.com/questions/25869428/classpath-resource-not-found-when-running-as-jar'>
        // Questions 25869428 (stackoverflow.com)</a>
        Notification notification = objectMapper.readValue(new ClassPathResource("mocks/notification.json").getInputStream(), Notification.class);
        notification.setContent(notification.getContent() + Instant.now());
        notification.setReceiver(userCode);
        notification.setHappenTime(LocalDateTime.now());

        webSocketSender.sendNotification(notification);

    }
}
