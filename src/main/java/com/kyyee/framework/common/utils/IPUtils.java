package com.kyyee.framework.common.utils;


import java.net.*;
import java.util.Enumeration;

public class IPUtils {
    /**
     * 获取本机的内网ip地址
     */
    public static String getInnetIp() {
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return null;
        }
        String innetIp = null;
        while (netInterfaces.hasMoreElements() && innetIp == null) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress ip = addresses.nextElement();
                if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {// 内网IP
                    innetIp = ip.getHostAddress();
                }
            }
        }
        return innetIp;
    }

    /**
     * 获取本机IP
     *
     * @return
     */
    public static String getLocalIP() {
        if (isWindowsOS()) {
            return getWindowsLocalIP();
        } else {
            return getLinuxLocalIP();
        }
    }

    /**
     * 判断操作系统是否是Windows
     *
     * @return
     */
    public static boolean isWindowsOS() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    /**
     * 获取Linux本机Ip
     *
     * @return
     */
    public static String getLinuxLocalIP() {
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return "";
        }
        String ip = "";
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (ni.getName().equals("eth0")) {
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ia = addresses.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;
                    }
                    ip = ia.getHostAddress();
                }
                break;
            }
        }
        return ip;
    }

    /**
     * 获取Windows本机Ip
     *
     * @return 本地IP地址
     */
    public static String getWindowsLocalIP() {
        InetAddress inet;
        try {
            inet = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            return "";
        }
        return inet.getHostAddress();
    }

    public static void main(String[] args) {
        System.out.println(getInnetIp());
        System.out.println(getLocalIP());
    }
}
