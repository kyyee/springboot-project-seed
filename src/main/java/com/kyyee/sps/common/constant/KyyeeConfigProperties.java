/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.common.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author kyyee
 * 通过application.properties 或 application.yml文件注入配置的配置类。
 */
@ConfigurationProperties(prefix = "kyyee.config")
@Data
public class KyyeeConfigProperties {

    private String projectName;
    private String author;
    private String email;
}
