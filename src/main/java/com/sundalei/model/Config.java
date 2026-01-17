package com.sundalei.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.config")
public record Config(String userId, String userAgent, String xBc, String sess, String appToken) {}
