package com.sundalei.config;

import com.sundalei.model.SigningRules;
import com.sundalei.model.UserCredentials;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({UserCredentials.class, SigningRules.class, ApiConfig.class})
@EnableFeignClients(basePackages = "com.sundalei.client")
public class AppConfig {}
