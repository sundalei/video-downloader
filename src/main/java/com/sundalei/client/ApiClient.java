package com.sundalei.client;

import com.sundalei.config.FeignConfig;
import com.sundalei.constant.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import tools.jackson.databind.JsonNode;

@FeignClient(name = "apiClient", url = "${app.api.base-url}", configuration = FeignConfig.class)
public interface ApiClient {

  @GetMapping(ApiConstants.ENDPOINT_SUBSCRIPTIONS)
  JsonNode listSubscriptions(
      @RequestParam("limit") int limit,
      @RequestParam("order") String order,
      @RequestParam("type") String type);

  @GetMapping("/users/{id}")
  JsonNode getUserInfo(@PathVariable("id") String userId);

  @GetMapping("/users/{id}/posts")
  JsonNode getPosts(
      @PathVariable("id") String userId,
      @RequestParam("limit") int limit,
      @RequestParam("order") String order,
      @RequestParam(value = "afterPublishTime", required = false) String afterPublishTime);
}
