package com.kyyee.framework.common.utils;


import com.fasterxml.jackson.databind.JsonNode;
import com.kyyee.framework.common.constant.GlobalConstant;
import com.kyyee.sps.common.utils.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * tangmingdong
 * Session处理类
 */
@Slf4j
public class SessionHelper {

    private SessionHelper() {
    }

    public static String getCookie(String key) {
        Cookie[] cookies = Objects.requireNonNull(getRequest()).getCookies();
        if (!ObjectUtils.isEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if (Objects.requireNonNull(key).equalsIgnoreCase(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static String getHeader(String key) {
        return Objects.requireNonNull(getRequest()).getHeader(key);
    }

    public static String getParameter(String key) {
        return Objects.requireNonNull(getRequest()).getParameter(key);
    }

    /**
     * 获取HttpServletRequest对象
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        return Objects.requireNonNull(getRequestAttributes()).getRequest();
    }

    /**
     * 获取HttpServletResponse对象
     *
     * @return
     */
    public static HttpServletResponse getResponse() {
        return Objects.requireNonNull(getRequestAttributes()).getResponse();
    }

    private static ServletRequestAttributes getRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    /**
     * 获取HttpSession
     *
     * @return
     */
    public static HttpSession getSession() {
        return getSession(true);
    }

    /**
     * 获取HttpSession
     *
     * @param b
     * @return
     */
    public static HttpSession getSession(boolean b) {
        return Objects.requireNonNull(getRequest()).getSession(b);
    }


    public static String getSession(String name) {
        return (String) getSessionObject(name);
    }

    public static Object getSessionObject(String name) {
        return Objects.requireNonNull(getSession()).getAttribute(name);
    }

    public static JsonNode getSessionJson(String name) {
        try {
            return JSON.mapper().readTree(Objects.requireNonNull(getSession(name)));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static <T> T getSessionObject(String name, Class<T> clazz) {
        return JSON.toBean(Objects.requireNonNull(getSession(name)), clazz);
    }

    public static void setSession(String name, Object obj) {
        Objects.requireNonNull(getSession()).setAttribute(name, obj);
    }

    public static void setSessionJson(String name, Object obj) {
        setSession(name, JSON.toString(Objects.requireNonNull(obj)));
    }

    /**
     * @param name
     * @param clazz 必须是对象 引用类型 不能是int 等
     * @param <T>
     * @return
     */
    @Deprecated
    public static <T> T getAttribute(String name, Class<T> clazz) {

        HttpSession session = SessionHelper.getSession(true);
        String json = (String) session.getAttribute(name);
        if (String.class.isAssignableFrom(clazz)) {
            return (T) json;
        }
        return JSON.toBean(json, clazz);
    }

    /**
     * 获取session中的信息，返回json
     *
     * @param name
     * @return
     */
    @Deprecated
    public static JsonNode getAttribute(String name) {
        HttpSession session = SessionHelper.getSession(true);
        String json = (String) session.getAttribute(name);
        return JSON.toJsonNode(json);
    }

    /**
     * @param name
     * @param obj
     * @param <T>  obj 必须是对象 引用类型 不能是 String  int 等
     */
    @Deprecated
    public static <T> void setAttribute(String name, T obj) {
        HttpSession session = SessionHelper.getSession(true);
        session.setAttribute(name, JSON.toString(obj));

    }


    /**
     * 获取当前请求的远程ip地址
     *
     * @return clientIp
     */
    public static String getRemoteIpAddress() {

        String ipAddress;
        // Cookies x_real_ip
        ipAddress = getCookie(GlobalConstant.COOKIES_X_REAL_IP);
        log.debug("Cookies x_real_ip:{}", ipAddress);
        if (isBlankOrUnKnown(ipAddress)) {
            // nginx X-Real-IP
            ipAddress = getHeader(GlobalConstant.X_REAL_IP);
            log.debug("nginx X-Real-IP:{}", ipAddress);
            if (isBlankOrUnKnown(ipAddress)) {
                // Squid X-Forwarded-For
                ipAddress = getHeader(GlobalConstant.X_FORWARDED_FOR);
                log.debug("Squid X-Forwarded-For:{}", ipAddress);
            }
            if (isBlankOrUnKnown(ipAddress)) {
                // apache http Proxy-Client-IP
                ipAddress = getHeader(GlobalConstant.PROXY_CLIENT_IP);
                log.debug("apache http Proxy-Client-IP:{}", ipAddress);
            }
            if (isBlankOrUnKnown(ipAddress)) {
                // apache http WL-Proxy-Client-IP
                ipAddress = getHeader(GlobalConstant.WL_PROXY_CLIENT_IP);
                log.debug("apache http WL-Proxy-Client-IP:{}", ipAddress);
            }
            if (isBlankOrUnKnown(ipAddress)) {
                // other proxy server
                ipAddress = getHeader(GlobalConstant.HTTP_CLIENT_IP);
                log.debug("other proxy server:{}", ipAddress);
            }
            if (isBlankOrUnKnown(ipAddress)) {
                // other proxy server
                ipAddress = getHeader(GlobalConstant.HTTP_X_FORWARDED_FOR);
                log.debug("other proxy server:{}", ipAddress);
            }
            if (isBlankOrUnKnown(ipAddress)) {
                // http/tcp remoteAddr
                ipAddress = Objects.requireNonNull(getRequest()).getRemoteAddr();
                log.debug("http/tcp remoteAddr:{}", ipAddress);
            } else {
                // 对于通过多个代理的情况, 第一个IP为客户端真实IP,多个IP按照','分割
                // "***.***.***.***".length() = 15
                if (ipAddress.length() > GlobalConstant.MAX_IP_LENGTH && ipAddress.contains(GlobalConstant.Symbol.COMMA)) {
                    final String[] ips = ipAddress.split(GlobalConstant.Symbol.COMMA);
                    for (String ip : ips) {
                        if (!GlobalConstant.UNKNOWN.equalsIgnoreCase(ip) && !GlobalConstant.NULL.equalsIgnoreCase(ip)
                            && ip.matches(GlobalConstant.IP_PATTERN_REGEXP)) {
                            ipAddress = ip;
                            log.debug("valid ip:{}", ipAddress);
                            break;
                        }
                    }
                }
            }
            if (ipAddress.equals(GlobalConstant.LOCALHOST_IP_16) || !ipAddress.matches(GlobalConstant.IP_PATTERN_REGEXP)) {
                ipAddress = GlobalConstant.LOCALHOST_IP;
            }
        }
        log.debug("final ip:{}", ipAddress);

        return ipAddress;
    }

    public static boolean isBlankOrUnKnown(String ipAddress) {
        return StringUtils.isEmpty(ipAddress) || GlobalConstant.UNKNOWN.equalsIgnoreCase(ipAddress);
    }
}
