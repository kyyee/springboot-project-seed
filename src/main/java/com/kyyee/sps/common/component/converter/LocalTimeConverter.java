/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.kyyee.sps.common.component.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author yanglei 00699 [yang.lei@unisinsight.com]
 * @date 2021/02/26 16:10
 * @since 1.0
 */
public class LocalTimeConverter implements Converter<String, LocalTime> {
    @Override
    public LocalTime convert(String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        return LocalTime.parse(source, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
