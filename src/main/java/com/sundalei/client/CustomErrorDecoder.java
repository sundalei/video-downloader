package com.sundalei.client;

import com.sundalei.exception.UpstreamApiException;
import com.sundalei.exception.UpstreamServerException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomErrorDecoder implements ErrorDecoder {

  @Override
  public Exception decode(String methodKey, Response response) {
    String body = "";
    try {
      if (response.body() != null) {
        body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
      }
    } catch (IOException e) {
      // ignore
    }

    if (response.status() >= 400 && response.status() < 500) {
      return new UpstreamApiException(body, response.status());
    } else if (response.status() >= 500) {
      return new UpstreamServerException("Upstream server error: " + response.reason());
    }

    return new ErrorDecoder.Default().decode(methodKey, response);
  }
}
