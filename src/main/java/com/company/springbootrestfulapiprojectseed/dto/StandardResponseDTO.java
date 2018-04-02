/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.dto;

/**
 * @param <T> 响应内容data类型
 * @author kyyee
 * 标准响应结构
 */
public class StandardResponseDTO<T> {

    private int result;
    private String message;
    private T data;

    public StandardResponseDTO() {
    }


    /**
     * 成功时返回
     *
     * @param result 响应码
     * @param data   响应体
     */
    public StandardResponseDTO(int result, T data) {
        this(result, "", data);
    }

    // 失败时返回
    public StandardResponseDTO(int result, String message, T data) {
        this.result = result;
        this.message = message;
        this.data = data;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
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
                "result=" + result +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
