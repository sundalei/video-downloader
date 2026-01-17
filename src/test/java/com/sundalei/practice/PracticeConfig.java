package com.sundalei.practice;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PracticeConfig {

  @Bean
  public RequestInterceptor practiceInterceptor() {
    return new PracticeInterceptor();
  }

  @Bean
  public ErrorDecoder practiceErrorDecoder() {
    return new PracticeErrorDecoder();
  }
}
