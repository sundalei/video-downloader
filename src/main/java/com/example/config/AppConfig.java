package com.example.config;

import com.example.model.DynamicRules;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  DynamicRules dynamicRules() {
    return new DynamicRules();
  }
}
