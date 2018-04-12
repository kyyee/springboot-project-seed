/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.v1.common;

import com.company.springbootrestfulapiprojectseed.v1.constant.ErrorCodeEnum;
import com.company.springbootrestfulapiprojectseed.v1.dto.StandardResponseDTO;

/**
 * @author kyyee
 */
public class HttpResponseUtil {
    public static StandardResponseDTO success(int result, Object data) {
        return new StandardResponseDTO<>(result, data);
    }

    public static StandardResponseDTO success(Object data) {
        return success(0, data);
    }

    public static StandardResponseDTO success(int result) {
        return success(result, null);
    }

    public static StandardResponseDTO success() {
        return success(0);
    }

    public static StandardResponseDTO error(ErrorCodeEnum errorCodeEnum) {
        return new StandardResponseDTO<>(errorCodeEnum.code(), errorCodeEnum.getMsg(), null);
    }

    public static StandardResponseDTO error(ErrorCodeEnum errorCodeEnum, String errMsg) {
        return new StandardResponseDTO<>(errorCodeEnum.code(), errorCodeEnum.getMsg() + errMsg, null);
    }
}
