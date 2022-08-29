package com.kyyee.framework.common.constant;

/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */


/**
 * description
 *
 * @author t17153 [tan.gang@h3c.com]
 * @date 2018/10/22 18:56
 * @since 1.0
 */
public final class GlobalConstant {

    public static final String AUTHORIZATION = "Authorization";
    public static final String HEADER_US_APP = "Us-App";
    public static final String USER = "Organization";
    public static final String SOURCE_AND_VERSION = "sourceAndVersion";
    public static final String BROADCAST = "broadcast";
    public static final String REDIS_PUSH_TOPIC = "push-message";
    public static final String PUSH_CHANNEL_ALL = "0";
    public static final String PUSH_CHANNEL_WEBSOCKET = "1";
    public static final String PUSH_CHANNEL_WECHAT = "2";
    public static final String PUSH_CHANNEL_GETUI = "3";
    public static final String PUSH_CHANNEL_WEBSOCKET_WECHAT = "12";
    public static final String PUSH_CHANNEL_WEBSOCKET_GETUI = "13";
    public static final String PUSH_CHANNEL_WECHAT_GETUI = "23";
    public static final String COOKIES_X_REAL_IP = "x_real_ip";
    public static final String X_REAL_IP = "X-Real-IP";
    public static final String X_FORWARDED_FOR = "x-forwarded-for";
    public static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
    public static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    public static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    public static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";
    public static final int MAX_IP_LENGTH = 15;
    public static final String LOCALHOST_IP = "127.0.0.1";
    public static final String LOCALHOST_IP_16 = "0:0:0:0:0:0:0:1";
    public static final String UNKNOWN = "unknown";
    public static final String NULL = "null";
    public static final String IP_PATTERN_REGEXP = "^(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])$";
    /**
     * 图片压缩高度
     */
    public static final int IMAGE_WIDTH = 1920;
    /**
     * 图片压缩宽度
     */
    public static final int IMAGE_HEIGHT = 1280;

    private GlobalConstant() {
    }

    public static final class Symbol {
        public static final String COMMA = ",";
        public static final String SPOT = ".";
        public static final String UNDER_LINE = "_";
        public static final String PER_CENT = "%";
        public static final String AT = "@";
        public static final String PIPE = "||";
        public static final String SHORT_LINE = "-";
        public static final String SPACE = " ";
        public static final String SLASH = "/";
        public static final String MH = ":";

        private Symbol() {
        }
    }

    public static final class Number {
        int THOUSAND_INT = 1000;
        int HUNDRED_INT = 100;
        int ONE_INT = 1;
        int TWO_INT = 2;
        int THREE_INT = 3;
        int FOUR_INT = 4;
        int FIVE_INT = 5;
        int SIX_INT = 6;
        int SEVEN_INT = 7;
        int EIGHT_INT = 8;
        int NINE_INT = 9;
        int TEN_INT = 10;
        int EIGHTEEN_INT = 18;

        private Number() {
        }
    }

    /**
     * 系统常量
     */
    public static final class Sys {
        /**
         * 超级管理员的用户ID
         */
        public static final Long SUPER_MANAGER_USER_ID = 1L;
        /**
         * 超级管理员的用户编号
         */
        public static final String SUPER_MANAGER_LOGIN_NAME = "admin";
        /**
         * 超级管理员角色ID
         */
        public static final Long SUPER_MANAGER_ROLE_ID = 1L;
        /**
         * 超级管理员组织ID
         */
        public static final Long SUPER_MANAGER_GROUP_ID = 1L;
        /**
         * 运营工作台ID
         */
        public static final Long OPER_APPLICATION_ID = 1L;
        /**
         * The constant MENU_ROOT.
         */
        public static final String MENU_ROOT = "root";

        private Sys() {
        }
    }

}
