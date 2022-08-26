package com.kyyee.framework.common.interceptor.user;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


public class UserHandler {
    private static final ThreadLocal<User> LOCAL = new ThreadLocal<>();

    private UserHandler() {
    }

    public static User getUser() {
        return LOCAL.get();
    }

    public static void setUser(User user) {
        LOCAL.set(user);
    }

    public static String userCode() {
        return LOCAL.get() == null ? null : LOCAL.get().getUserCode();
    }

    public static String userName() {
        return LOCAL.get() == null ? null : LOCAL.get().getUserName();
    }

    public static void remove() {
        LOCAL.remove();
    }
}
