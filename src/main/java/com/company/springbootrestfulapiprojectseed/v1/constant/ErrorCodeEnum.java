/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.v1.constant;

/**
 * @author kyyee
 * 异常返回前端的错误码枚举
 */
public enum ErrorCodeEnum {
    UNAUTHORIZED(-401, "认证失败"),
    FORBIDDEN(-403, "拒绝访问"),
    NOT_FOUND(-404, "找不到资源"),
    CANNOT_ACCESS_DATABASE(-500, "连接不到数据库。"),
    NULL_POINTER_EXCEPTION(-500, "空指针异常。"),
    IO_EXCEPTION(-500, "IO异常。"),
    UNKNOWN_EXCEPTION(-500, "未明确的异常。"),
    SERVICE_EXCEPTION(-500, "服务异常。"),
    ILLEGAL_PARAMETER_EXCEPTION(-1000, "参数校验失败。"),
    IMPORT_VM_EXCEPTION(-500, "导入虚拟机异常。"),
    JSON_PARSE_EXCEPTION(-500, "JSON 字符串解析异常。"),
    LIFECYCLE_EXCEPTION(-500, "虚拟机生命周期管理异常。"),
    LOAD_EXCEPTION(-500, "获取负载情况异常。"),
    NODE_CONNECT_EXCEPTION(-500, "连接远端节点异常。"),
    OS_NOT_SUPPORT_EXCEPTION(-1000, "不支持的操作系统。"),
    REMOTE_MANAGE_EXCEPTION(-500, "虚拟机远程管理异常。"),
    SERVER_NOT_FOUND_EXCEPTION(-1000, "未知服务器。"),
    SNAP_SHOT_EXCEPTION(-500, "虚拟机镜像生命周期管理异常。"),
    STORAGE_EXCEPTION(-500, "存储池或虚拟机镜像异常。"),
    VM_NOT_FOUND_EXCEPTION(-1000, "未知虚拟机。");

    private final int code;
    private final String msg;

    ErrorCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int code() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    @Override
    public String toString() {
        return "ErrorCodeEnum{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
