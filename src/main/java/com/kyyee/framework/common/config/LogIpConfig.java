package com.kyyee.framework.common.config;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.kyyee.framework.common.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogIpConfig extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        try {
            return IpUtils.getInnetIp();
        } catch (Exception e) {
            log.error("获取日志Ip异常", e);
        }
        return null;
    }
}
