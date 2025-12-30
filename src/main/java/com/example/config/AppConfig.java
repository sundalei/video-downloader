package com.example.config;

import com.example.exception.UpstreamApiException;
import com.example.exception.UpstreamServerException;
import com.example.model.Config;
import com.example.model.DynamicRules;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Bean
  RestClient restClient(RestClient.Builder builder) {
    return builder
        .requestFactory(new HttpComponentsClientHttpRequestFactory())
        .defaultStatusHandler(
            HttpStatusCode::is4xxClientError,
            (request, response) -> {
              String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
              // Throw custom exception with the upstream error message
              throw new UpstreamApiException(body, response.getStatusCode().value());
            })
        .defaultStatusHandler(
            HttpStatusCode::is5xxServerError,
            (request, response) -> {
              throw new UpstreamServerException(
                  "Upstream server error: " + response.getStatusText());
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
