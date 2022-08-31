package com.kyyee.sps.common.component.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeConverter implements Converter<String, LocalTime> {
    @Override
    public LocalTime convert(String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        return LocalTime.parse(source, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
