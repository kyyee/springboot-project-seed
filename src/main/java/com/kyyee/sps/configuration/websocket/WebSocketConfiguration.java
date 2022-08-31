package com.kyyee.sps.configuration.websocket;

import com.kyyee.sps.common.component.interceptor.HttpSessionIdHandshakeInterceptor;
import com.kyyee.sps.common.component.interceptor.PreSendChannelInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.annotation.Resource;

/**
 * <p>
 * Websocket配置文件
 *
 * @author zhoubiao [KF.zhoubiao@h3c.com]
 * @date 2018/9/18 9:25
 * @since 1.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Value("${kyyee.websocket.base-path:NA}")
    private String endpoint;

    @Resource
    private PreSendChannelInterceptor preSendChannelInterceptor;

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    /**
     * 配置websocket接入点和连接拦截器
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(endpoint)
            .setAllowedOrigins("*") // 解决跨域问题
            .withSockJS()
            .setInterceptors(new HttpSessionIdHandshakeInterceptor());
    }

    /**
     * 配置消息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 表明在topic、queue、user这三个域上可以向客户端发消息
        registry.enableSimpleBroker("/topic", "/queue", "/user");
        // 客户端向服务端发起请求时，需要以/app为前缀
        registry.setApplicationDestinationPrefixes("/app");
        // 给指定用户发送一对一的消息前缀是/user
        registry.setUserDestinationPrefix("/user");

    }

    /**
     * 消息传输参数配置
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(8192) // 设置消息字节数大小
            .setSendBufferSizeLimit(8192) // 设置消息缓存大小
            .setSendTimeLimit(10000); // 设置消息发送时间限制毫秒
    }

    /**
     * 输入通道参数设置
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        configureClient(registration);
    }

    /**
     * 输出通道参数设置
     */
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        configureClient(registration);
    }

    private void configureClient(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(4).maxPoolSize(8);
        registration.interceptors(preSendChannelInterceptor);
    }

}
