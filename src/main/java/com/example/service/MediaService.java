package com.example.service;

import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Service
public class MediaService {

  private static final Logger log = LoggerFactory.getLogger(MediaService.class);
  private static final String API_URL = "https://onlyfans.com/api2/v2";

  private final RestClient restClient;
  private final ApiAuthService apiAuthService;

  public MediaService(RestClient restClient, ApiAuthService apiAuthService) {
    this.restClient = restClient;
    this.apiAuthService = apiAuthService;
  }

  public void listMediaContents(String profile) {
    log.info("gather posts of {}", profile);

    // Get user info
    JsonNode userInfo = getUserInfo(profile);
    if (userInfo == null || !userInfo.has("id")) {
      log.error("Could not find user: {}", profile);
      return;
    }
    String userId = userInfo.get("id").asString();
    log.info("Found user ID: {}", userId);
  }

  private JsonNode getUserInfo(String profile) {
    String endpoint = "/users/" + profile;
    Map<String, String> queryParams = Collections.emptyMap();
    HttpHeaders headers = apiAuthService.createSignedHeaders(endpoint, queryParams);

    ResponseEntity<JsonNode> response =
        restClient
            .get()
            .uri(API_URL + endpoint)
            .headers(h -> h.addAll(headers))
            .retrieve()
            .toEntity(JsonNode.class);

    return response.getBody();
  }
}
