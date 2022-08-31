package com.kyyee.sps.common.component.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * websocket握手（handshake）接口 Created by earl on 2017/4/17.
 */
@Slf4j
public class HttpSessionIdHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    /**
     * 登录的客户端IP
     **/
    public static final String REMOTE_ADDRESS = "remoteAddress";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        // 解决The extension [x-webkit-deflate-frame] is not supported问题
        log.info("beforeHandshake remote address: {}", request.getRemoteAddress());
        attributes.put(REMOTE_ADDRESS, request.getRemoteAddress());
        if (request.getHeaders().containsKey("Sec-WebSocket-Extensions")) {
            request.getHeaders().set("Sec-WebSocket-Extensions", "permessage-deflate");
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

}
