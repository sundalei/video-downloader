package com.example.exception;

public class UpstreamServerException extends RuntimeException {
  public UpstreamServerException(String message) {
    super(message);
  }
}
