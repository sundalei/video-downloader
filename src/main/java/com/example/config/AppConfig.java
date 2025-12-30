package com.example.config;

import com.example.model.Config;
import com.example.model.DynamicRules;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Bean
  DynamicRules dynamicRules() throws IOException {
    Resource resource = new ClassPathResource("dynamic_rules.json");
    return objectMapper.readValue(resource.getInputStream(), DynamicRules.class);
  }

  @Bean
  Config config() throws IOException {
    Resource resource = new ClassPathResource("config.json");
    return objectMapper.readValue(resource.getInputStream(), Config.class);
  }
}
