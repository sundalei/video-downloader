package com.sundalei.model;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rules")
public record SigningRules(
    String staticParam, List<Integer> checksumIndexes, Integer checksumConstant, String format) {}
