package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Media {
  private Long id;
  private String type;
  private boolean canView;
  private MediaFiles files;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isCanView() {
    return canView;
  }

  public void setCanView(boolean canView) {
    this.canView = canView;
  }

  public MediaFiles getFiles() {
    return files;
  }

  public void setFiles(MediaFiles files) {
    this.files = files;
  }
}
