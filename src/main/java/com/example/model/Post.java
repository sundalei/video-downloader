package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {
  private Long id;
  private String postedAt;
  private String postedAtPrecise;
  private boolean canViewMedia;
  private List<Media> media;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPostedAt() {
    return postedAt;
  }

  public void setPostedAt(String postedAt) {
    this.postedAt = postedAt;
  }

  public String getPostedAtPrecise() {
    return postedAtPrecise;
  }

  public void setPostedAtPrecise(String postedAtPrecise) {
    this.postedAtPrecise = postedAtPrecise;
  }

  public boolean isCanViewMedia() {
    return canViewMedia;
  }

  public void setCanViewMedia(boolean canViewMedia) {
    this.canViewMedia = canViewMedia;
  }

  public List<Media> getMedia() {
    return media;
  }

  public void setMedia(List<Media> media) {
    this.media = media;
  }
}
