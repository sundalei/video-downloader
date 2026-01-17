package com.sundalei.client;

import com.sundalei.service.ApiAuthService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

@Slf4j
public class AuthRequestInterceptor implements RequestInterceptor {

  private final ApiAuthService apiAuthService;

  public AuthRequestInterceptor(ApiAuthService apiAuthService) {
    this.apiAuthService = apiAuthService;
  }

  @Override
  public void apply(RequestTemplate template) {
    String path = template.path();
    log.info("path: {}", path);

    Map<String, String> queryParams = new LinkedHashMap<>();
    if (template.queries() != null) {
      template
          .queries()
          .forEach(
              (k, v) -> {
                if (v != null && !v.isEmpty()) {
                  queryParams.put(k, v.iterator().next());
                }
              });
    }

    HttpHeaders headers = apiAuthService.createSignedHeaders(path, queryParams);
    log.info("headers: {}", headers);
    headers.forEach(
        (k, v) -> {
          if (v != null && !v.isEmpty()) {
            template.header(k, v);
          }
        });
  }
}
