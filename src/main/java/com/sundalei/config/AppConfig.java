package com.sundalei.config;

import com.sundalei.exception.UpstreamApiException;
import com.sundalei.exception.UpstreamServerException;
import com.sundalei.model.SigningRules;
import com.sundalei.model.UserCredentials;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties({UserCredentials.class, SigningRules.class, ApiConfig.class})
public class AppConfig {

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
}
