package com.sundalei.service;

import com.sundalei.config.ApiConfig;
import com.sundalei.constant.ApiConstants;
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
  private final ApiConfig apiConfig;

  public SubscriptionService(
      RestClient restClient, ApiAuthService apiAuthService, ApiConfig apiConfig) {
    this.restClient = restClient;
    this.apiAuthService = apiAuthService;
    this.apiConfig = apiConfig;
  }

  public List<String> listSubscriptions() {
    final String endpoint = ApiConstants.ENDPOINT_SUBSCRIPTIONS;
    UriComponentsBuilder builder =
        UriComponentsBuilder.fromUriString(apiConfig.baseUrl() + endpoint)
            .queryParam("limit", "50")
            .queryParam("order", "publish_date_asc")
            .queryParam("type", "active");
    Map<String, String> queryParams = builder.build().getQueryParams().toSingleValueMap();
    HttpHeaders headers = apiAuthService.createSignedHeaders(endpoint, queryParams);

    ResponseEntity<JsonNode> response =
        restClient
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
