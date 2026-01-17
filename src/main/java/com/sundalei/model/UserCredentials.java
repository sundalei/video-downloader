package com.sundalei.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.config")
public record UserCredentials(
    String userId, String userAgent, String xBcToken, String sess, String appToken) {}
