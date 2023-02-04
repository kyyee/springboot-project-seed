package com.kyyee.sps.common.component.interceptor;

import com.kyyee.framework.common.interceptor.user.User;
import com.kyyee.sps.common.component.cache.UserCache;
import com.kyyee.sps.common.utils.BeanCopyUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import static org.springframework.messaging.support.NativeMessageHeaderAccessor.NATIVE_HEADERS;

/**
 * websocket拦截器
 */
@Slf4j
@Component
public class PreSendChannelInterceptor implements ChannelInterceptor {

    /**
     * 登录的客户端IP
     **/
    public static final String REMOTE_ADDRESS = "remoteAddress";

    /*** 终端唯一标识键 */
    public static final String ID_KEY = "idKey";

    @Resource
    private ThreadPoolTaskExecutor customTaskExecutor;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        Map<?, ?> nativeHeadersMap = (Map<?, ?>) message.getHeaders().get(NATIVE_HEADERS);

        StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);
        // ignore non-STOMP messages like heartbeat messages
        if (sha.getCommand() == null) {
            return message;
        }
        // 判断客户端的连接状态
        if (sha.getCommand() == StompCommand.CONNECT) {
            String clientId = "";
            List<?> clientIdList = (List<?>) nativeHeadersMap.get("id");
            if (clientIdList != null) {
                clientId = clientIdList.get(0).toString();
            }

            int outer = 0;
            List<?> outerList = (List<?>) nativeHeadersMap.get("outer");
            if (outerList != null) {
                outer = Integer.parseInt(outerList.get(0).toString());
            }

            // 从user取值
            User user = null;
            List<?> userList = (List<?>) nativeHeadersMap.get("user");
            if (userList != null) {
                String userStr = userList.get(0).toString();

                user = handleUserInfo(userStr);
            }
            log.debug("user:{}, outer:{}", user, outer);

            if (!ObjectUtils.isEmpty(user)) {
                final UserCache.User cacheUser = BeanCopyUtils.convert(user, UserCache.User.class);
                cacheUser.setOuter(outer);
                UserCache.set(clientId, cacheUser);

                // websocket建立链接后开线程处理该用户的历史消息
                // 即从messageMetadata中读取用户最后在线时间后的消息存入messageStatus
                customTaskExecutor.execute(() -> handleMessageMetadataByUserLastOnlineTime(cacheUser));
            }
        }
        return message;
    }

    private User handleUserInfo(String authorization) {
        // 编码转换
//        authorization = EscapeUtils.unescape(EscapeUtils.unescape(authorization));
        try {
            authorization = URLDecoder.decode(authorization, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // ignore e
        }

        String[] array = authorization.split("&");

        User user = new User();

        for (String line : array) {
            String[] keyValue = line.split(":");
            if (keyValue.length < 2) {
                continue;
            }
            if ("usercode".equalsIgnoreCase(keyValue[0])) {
                user.setUserCode(keyValue[1]);
            }

            if ("username".equalsIgnoreCase(keyValue[0])) {
                user.setUserName(keyValue[1]);
            }
        }

        //处理其它用户信息
        return user;
    }

    void handleMessageMetadataByUserLastOnlineTime(UserCache.User user) {
        log.info("login user: {}", user.getUserCode());
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {

        Map<?, ?> nativeHeadersMap = (Map<?, ?>) message.getHeaders().get(NATIVE_HEADERS);

        StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);
        // ignore non-STOMP messages like heartbeat messages
        if (sha.getCommand() == null) {
            return;
        }
        String remoteAddress = "unknown";
        if (sha.getSessionAttributes() != null
            && sha.getSessionAttributes().get(REMOTE_ADDRESS) != null) {
            remoteAddress = sha.getSessionAttributes().get(REMOTE_ADDRESS).toString();
        }

        String idString = null;
        if (sha.getSessionAttributes().get(ID_KEY) == null && nativeHeadersMap != null) {

            List<?> idList = (List<?>) nativeHeadersMap.get("id");
            if (idList != null) {
                idString = idList.get(0).toString();
                sha.getSessionAttributes().put(ID_KEY, idList.get(0).toString());
            }
        }

        // 判断客户端的连接状态
        switch (sha.getCommand()) {
            case CONNECT:
                log.info("{} presenceChannelInterceptor CONNECT, id:{}", remoteAddress, idString);
                break;
            case DISCONNECT:
                if (sha.getSessionAttributes().get(ID_KEY) != null) {
                    String id = sha.getSessionAttributes().get(ID_KEY).toString();
                    log.info("{} presenceChannelInterceptor DISCONNECT, id:{}", remoteAddress, id);

                    // 更新用户最后在线时间
                    final UserCache.User user = UserCache.get(id);
                    if (!ObjectUtils.isEmpty(user)) {
                        UserCache.remove(id);
                    }
                }
                break;
            default:
                break;
        }
    }

}
