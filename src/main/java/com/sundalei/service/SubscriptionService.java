package com.sundalei.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;

@Service
public class SubscriptionService {
  private final RestClient restClient;
  private final ApiAuthService apiAuthService;
  private static final String API_URL = "https://onlyfans.com/api2/v2";

  public SubscriptionService(RestClient restClient, ApiAuthService apiAuthService) {
    this.restClient = restClient;
    this.apiAuthService = apiAuthService;
  }

  public List<String> listSubscriptions() {
    final String endpoint = "/subscriptions/subscribes";
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(API_URL + endpoint)
        .queryParam("limit", "50")
        .queryParam("order", "publish_date_asc")
        .queryParam("type", "active");
    Map<String, String> queryParams = builder.build().getQueryParams().toSingleValueMap();
    HttpHeaders headers = apiAuthService.createSignedHeaders(endpoint, queryParams);

    ResponseEntity<JsonNode> response = restClient
        .get()
        .uri(builder.toUriString())
        .headers(h -> h.addAll(headers))
        .retrieve()
        .toEntity(JsonNode.class);

    List<String> usernames = new ArrayList<>();
    if (response.getBody() != null && response.getBody().isArray()) {
      for (JsonNode node : response.getBody()) {
        if (node.has("username")) {
          usernames.add(node.get("username").asString());
        }
      }
    }
    return usernames;
  }
}
