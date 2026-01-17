package com.sundalei.dto;

public class PostUrlResponse {
  private String postedAt;
  private String url;

  public PostUrlResponse(String postedAt, String url) {
    this.postedAt = postedAt;
    this.url = url;
  }

  public String getPostedAt() {
    return postedAt;
  }

  public void setPostedAt(String postedAt) {
    this.postedAt = postedAt;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
