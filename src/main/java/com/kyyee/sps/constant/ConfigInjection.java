/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author kyyee
 * 通过application.properties 或 application.yml文件注入配置的配置类。
 */
@Component
@ConfigurationProperties(prefix = "vm-config")
public class ConfigInjection {

    private String projectName;
    private String author;
    private String email;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
