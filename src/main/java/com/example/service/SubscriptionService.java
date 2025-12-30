package com.example.service;

import com.example.model.Config;
import com.example.model.DynamicRules;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;

@Service
public class SubscriptionService {
  private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);

  private final DynamicRules dynamicRules;
  private final Config config;
  private final RestClient restClient;
  private static final String API_URL = "https://onlyfans.com/api2/v2";

  public SubscriptionService(DynamicRules dynamicRules, Config config, RestClient restClient) {
    this.dynamicRules = dynamicRules;
    this.config = config;
    this.restClient = restClient;
  }

  public List<String> listSubscriptions() {
    final String endpoint = "/subscriptions/subscribes";
    UriComponentsBuilder builder =
        UriComponentsBuilder.fromUriString(API_URL + endpoint)
            .queryParam("limit", "50")
            .queryParam("order", "publish_date_asc")
            .queryParam("type", "active");
    Map<String, String> queryParams = builder.build().getQueryParams().toSingleValueMap();
    HttpHeaders headers = createSignedHeaders(endpoint, queryParams);

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

  private HttpHeaders createSignedHeaders(String path, Map<String, String> queryParams) {
    String fullPath = "/api2/v2" + path;

    String query = "";
    if (queryParams != null && !queryParams.isEmpty()) {
      List<String> pairs = new ArrayList<>();
      queryParams.forEach((k, v) -> pairs.add(k + "=" + v));
      query = String.join("&", pairs);
      fullPath = fullPath + "?" + query;
    }

    String unixtime = String.valueOf(Instant.now().getEpochSecond());
    String message =
        String.join("\n", dynamicRules.getStaticParam(), unixtime, fullPath, config.getUserId());
    String sha1Sign = DigestUtils.sha1Hex(message.getBytes(StandardCharsets.UTF_8));

    byte[] sha1Bytes = sha1Sign.getBytes(StandardCharsets.US_ASCII);
    int checksum = 0;
    for (Integer idx : dynamicRules.getChecksumIndexes()) {
      if (idx < sha1Bytes.length) {
        checksum += sha1Bytes[idx];
      }
    }
    checksum += dynamicRules.getChecksumConstant();

    String sign = String.format(dynamicRules.getFormat(), sha1Sign, Math.abs(checksum));

    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", "application/json, text/plain, */*");
    headers.set("app-token", config.getAppToken());
    headers.set("User-Agent", config.getUserAgent());
    headers.set("x-bc", config.getxBc());
    headers.set("user-id", config.getUserId());
    headers.set("Cookie", "auh_id=" + config.getUserId() + "; sess=" + config.getSessionCookie());
    headers.set("sign", sign);
    headers.set("time", unixtime);

    return headers;
  }
}
