package com.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class DynamicRules {
  @JsonProperty("static_param")
  private String staticParam;

  @JsonProperty("checksum_indexes")
  private List<Integer> checksumIndexes;

  @JsonProperty("checksum_constant")
  private Integer checksumConstant;

  @JsonProperty("format")
  private String format;

  public String getStaticParam() {
    return staticParam;
  }

  public void setStaticParam(String staticParam) {
    this.staticParam = staticParam;
  }

  public List<Integer> getChecksumIndexes() {
    return checksumIndexes;
  }

  public void setChecksumIndexes(List<Integer> checksumIndexes) {
    this.checksumIndexes = checksumIndexes;
  }

  public Integer getChecksumConstant() {
    return checksumConstant;
  }

  public void setChecksumConstant(Integer checksumConstant) {
    this.checksumConstant = checksumConstant;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }
}
