package com.sundalei.service;

import com.sundalei.client.ApiClient;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

@Service
public class SubscriptionService {
  private final ApiClient apiClient;

  public SubscriptionService(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public List<String> listSubscriptions() {
    JsonNode response = apiClient.listSubscriptions(50, "publish_date_asc", "active");

    List<String> usernames = new ArrayList<>();
    if (response != null && response.isArray()) {
      for (JsonNode node : response) {
        if (node.has("username")) {
          usernames.add(node.get("username").asString());
        }
      }
    }
    return usernames;
  }
}
