package com.sundalei.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // Handle 4xx errors from Upstream
  @ExceptionHandler(UpstreamApiException.class)
  public ResponseEntity<Map<String, Object>> handleUpstreamClientError(UpstreamApiException ex) {
    Map<String, Object> errorBody = new HashMap<>();
    errorBody.put("timestamp", LocalDateTime.now());
    errorBody.put("error", "Upstream Service Error");
    errorBody.put("details", ex.getMessage());

    // We return BAD_REQUEST or BAD_GATEWAY depending on your preference
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
  }

  // Handle 5xx errors from Upstream
  @ExceptionHandler(UpstreamServerException.class)
  public ResponseEntity<Map<String, Object>> handleUpstreamServerError(UpstreamServerException ex) {
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body(Map.of("error", "Upstream provider is currently unavailable"));
  }

  // Handle Network / Connection Errors
  @ExceptionHandler(ResourceAccessException.class)
  public ResponseEntity<Map<String, Object>> handleNetworkError(ResourceAccessException ex) {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(
            Map.of(
                "error", "Connection Failed",
                "message",
                "Unable to establish connection to provider. Possible IP block or timeout."));
  }
}
