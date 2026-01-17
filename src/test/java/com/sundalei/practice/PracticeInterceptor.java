package com.sundalei.practice;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PracticeInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        log.info("PracticeInterceptor: Adding secret header to {}", template.url());
        template.header("X-Practice-Secret", "SuperSecretValue");
    }
}
