package com.sundalei.practice;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PracticeErrorDecoder implements ErrorDecoder {
  @Override
  public Exception decode(String methodKey, Response response) {
    log.info("PracticeErrorDecoder: Caught error status {}", response.status());

    if (response.status() == 404) {
      return new RuntimeException("Practice: Resource not found!");
    }

    return new ErrorDecoder.Default().decode(methodKey, response);
  }
}
