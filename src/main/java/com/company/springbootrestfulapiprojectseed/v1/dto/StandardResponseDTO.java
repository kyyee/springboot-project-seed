/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.v1.dto;

/**
 * @param <T> 响应内容data类型
 * @author kyyee
 * 标准响应结构
 */
public class StandardResponseDTO<T> {

    private int code;
    private String message;
    private T data;

    public StandardResponseDTO() {
    }

    /**
     * 成功时返回
     *
     * @param code 响应码
     * @param data 响应体
     */
    public StandardResponseDTO(int code, T data) {
        this(code, "", data);
    }

    /**
     * 失败时返回
     *
     * @param code    响应码
     * @param message 错误信息详细描述
     * @param data    响应体
     */
    public StandardResponseDTO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "StandardResponseDTO{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
