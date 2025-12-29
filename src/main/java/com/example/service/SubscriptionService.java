package com.example.service;

import com.example.model.DynamicRules;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SubscriptionService {
  private final DynamicRules dynamicRules;
  private static final String API_URL = "https://onlyfans.com/api2/v2";

  public SubscriptionService(DynamicRules dynamicRules) {
    this.dynamicRules = dynamicRules;
  }

  public void listSubscriptions() {
    final String endpoint = "/subscriptions/subscribes";
    UriComponentsBuilder builder =
        UriComponentsBuilder.fromUriString(API_URL + endpoint)
            .queryParam("limit", "50")
            .queryParam("order", "publish_date_asc")
            .queryParam("type", "active");
    Map<String, String> queryParams = builder.build().getQueryParams().toSingleValueMap();

    createSignedHeaders(endpoint, queryParams);
    System.out.println(endpoint);
  }

  private void createSignedHeaders(String path, Map<String, String> queryParams) {
    String fullPath = "/api2/v2" + path;

    String query = "";
    if (queryParams != null && !queryParams.isEmpty()) {
      List<String> pairs = new ArrayList<>();
      queryParams.forEach((k, v) -> pairs.add(k + "=" + v));
      query = String.join("&", pairs);
      fullPath = fullPath + "?" + query;
    }

    String unixtime = String.valueOf(Instant.now().getEpochSecond());
    String message = String.join("\n", dynamicRules.toString(), "b");
    System.out.println("message: " + message);
    System.out.println("unixtime: " + unixtime);
    System.out.println(fullPath);
    System.out.println(queryParams);
  }
}
