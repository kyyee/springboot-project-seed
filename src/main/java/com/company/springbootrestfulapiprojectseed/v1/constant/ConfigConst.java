/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.v1.constant;

/**
 * @author kyyee
 * 常量配置类
 */
public class ConfigConst {
    public final static String COLON = ":"; // 冒号
    public final static String SPACE = " "; //空格

    // 验证远程主机是否在线，连接默认超时 200 毫秒以上
    public final static int DEFAULT_TIMEOUT = 200;

    // 操作系统类型
    public final static String OS_NAME = "os.name";
    public final static String WINDOWS = "windows";
    public final static String LINUX = "linux";
    public final static String MAC = "mac";

    // 默认文本文件编码字符集
    public final static String CHARSET_ASCII = "ASCII";
    // 默认网络传输编码字符集
    public final static String CHARSET_UTF8 = "UTF-8";
    // http头配置
    public final static String CONTENT_TYPE = "Content-Type";

    // 解析 HttpServletRequest 头里面的 jwt 字段
    public final static String JWT_FOR_REQUEST_HEADER = "jwt";

    // 异步执行成功
    public final static String SUCCESS = "success";

    public final static long FIXED_RATE_THRESHOLD = 5L * 60 * 1000;

    // 异步处理线程配置
    public final static int CORE_POOL_SIZE = 10;
    public final static int MAX_POOL_SIZE = 10;
    public final static int QUEUE_CAPACITY = 10;
}
