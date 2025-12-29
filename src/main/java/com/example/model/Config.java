package com.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Config {
  @JsonProperty("user_id")
  private String userId;

  @JsonProperty("user_agent")
  private String userAgent;

  @JsonProperty("x_bc")
  private String xBc;

  @JsonProperty("sess")
  private String sessionCookie;

  @JsonProperty("app_token")
  private String appToken;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public String getxBc() {
    return xBc;
  }

  public void setxBc(String xBc) {
    this.xBc = xBc;
  }

  public String getSessionCookie() {
    return sessionCookie;
  }

  public void setSessionCookie(String sessionCookie) {
    this.sessionCookie = sessionCookie;
  }

  public String getAppToken() {
    return appToken;
  }

  public void setAppToken(String appToken) {
    this.appToken = appToken;
  }
}
