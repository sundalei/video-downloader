package com.example.exception;

public class UpstreamApiException extends RuntimeException {
  private final int statusCode;

  public UpstreamApiException(String message, int statusCode) {
    super(message);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }
}
