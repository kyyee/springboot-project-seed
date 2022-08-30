/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.common.utils;



import java.io.IOException;
import java.net.InetAddress;

/**
 * @author kyyee
 * 与 ip 相关的公共方法
 */
public class IpUtils {

    /**
     * 根据ip验证网络是否连通
     *
     * @param ip ip
     * @return 连通返回true，反之返回false
     * @throws IOException IO异常。
     */
    public static boolean ping(String ip) throws IOException {
        return InetAddress.getByName(ip)
            // 验证远程主机是否在线，连接默认超时 200 毫秒以上
            .isReachable(2000);
    }
}
