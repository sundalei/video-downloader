package com.sundalei.service;

import com.sundalei.model.Config;
import com.sundalei.model.DynamicRules;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class ApiAuthService {
  private final DynamicRules dynamicRules;
  private final Config config;

  public ApiAuthService(DynamicRules dynamicRules, Config config) {
    this.dynamicRules = dynamicRules;
    this.config = config;
  }

  public HttpHeaders createSignedHeaders(String path, Map<String, String> queryParams) {
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
        String.join("\n", dynamicRules.staticParam(), unixtime, fullPath, config.userId());
    String sha1Sign = DigestUtils.sha1Hex(message.getBytes(StandardCharsets.UTF_8));

    byte[] sha1Bytes = sha1Sign.getBytes(StandardCharsets.US_ASCII);
    int checksum = 0;
    for (Integer idx : dynamicRules.checksumIndexes()) {
      if (idx < sha1Bytes.length) {
        checksum += sha1Bytes[idx];
      }
    }
    checksum += dynamicRules.checksumConstant();

    String sign = String.format(dynamicRules.format(), sha1Sign, Math.abs(checksum));

    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", "application/json, text/plain, */*");
    headers.set("app-token", config.appToken());
    headers.set("User-Agent", config.userAgent());
    headers.set("x-bc", config.xBc());
    headers.set("user-id", config.userId());
    headers.set("Cookie", "auh_id=" + config.userId() + "; sess=" + config.sessionCookie());
    headers.set("sign", sign);
    headers.set("time", unixtime);

    return headers;
  }
}
