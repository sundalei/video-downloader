package com.sundalei.config;

import com.sundalei.client.AuthRequestInterceptor;
import com.sundalei.client.CustomErrorDecoder;
import com.sundalei.service.ApiAuthService;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

  @Bean
  public RequestInterceptor requestInterceptor(ApiAuthService apiAuthService) {
    return new AuthRequestInterceptor(apiAuthService);
  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return new CustomErrorDecoder();
  }
}
