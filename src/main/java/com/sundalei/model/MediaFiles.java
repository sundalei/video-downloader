package com.sundalei.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MediaFiles {
  private Full full;

  public Full getFull() {
    return full;
  }

  public void setFull(Full full) {
    this.full = full;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Full {
    private String url;

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }
  }
}
