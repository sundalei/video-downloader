package com.sundalei.service;

import com.sundalei.constant.ApiConstants;
import com.sundalei.model.SigningRules;
import com.sundalei.model.UserCredentials;
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
  private final SigningRules signingRules;
  private final UserCredentials userCredentials;

  public ApiAuthService(SigningRules signingRules, UserCredentials userCredentials) {
    this.signingRules = signingRules;
    this.userCredentials = userCredentials;
  }

  public HttpHeaders createSignedHeaders(String path, Map<String, String> queryParams) {
    String fullPath = ApiConstants.API_V2_PATH + path;

    String query = "";
    if (queryParams != null && !queryParams.isEmpty()) {
      List<String> pairs = new ArrayList<>();
      queryParams.forEach((k, v) -> pairs.add(k + "=" + v));
      query = String.join("&", pairs);
      fullPath = fullPath + "?" + query;
    }

    String unixtime = String.valueOf(Instant.now().getEpochSecond());
    String message =
        String.join("\n", signingRules.staticParam(), unixtime, fullPath, userCredentials.userId());
    String sha1Sign = DigestUtils.sha1Hex(message.getBytes(StandardCharsets.UTF_8));

    byte[] sha1Bytes = sha1Sign.getBytes(StandardCharsets.US_ASCII);
    int checksum = 0;
    for (Integer idx : signingRules.checksumIndexes()) {
      if (idx < sha1Bytes.length) {
        checksum += sha1Bytes[idx];
      }
    }
    checksum += signingRules.checksumConstant();

    String sign = String.format(signingRules.format(), sha1Sign, Math.abs(checksum));

    HttpHeaders headers = new HttpHeaders();
    headers.set(ApiConstants.HEADER_ACCEPT, "application/json, text/plain, */*");
    headers.set(ApiConstants.HEADER_APP_TOKEN, userCredentials.appToken());
    headers.set(ApiConstants.HEADER_USER_AGENT, userCredentials.userAgent());
    headers.set(ApiConstants.HEADER_X_BC, userCredentials.xBcToken());
    headers.set(ApiConstants.HEADER_USER_ID, userCredentials.userId());
    headers.set(
        ApiConstants.HEADER_COOKIE,
        "auh_id=" + userCredentials.userId() + "; sess=" + userCredentials.sess());
    headers.set(ApiConstants.HEADER_SIGN, sign);
    headers.set(ApiConstants.HEADER_TIME, unixtime);

    return headers;
  }
}
