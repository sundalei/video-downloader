package com.example.config;

import com.example.model.Config;
import com.example.model.DynamicRules;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {
  private static final Logger log = LoggerFactory.getLogger(AppConfig.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Bean
  RestClient restClient(RestClient.Builder builder) {
    return builder
        .requestFactory(new HttpComponentsClientHttpRequestFactory())
        .defaultStatusHandler(
            HttpStatusCode::isError,
            (request, response) -> {
              log.error("API Error Status: {}", response.getStatusCode());
            })
        .build();
  }

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
