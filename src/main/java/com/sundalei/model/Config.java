package com.sundalei.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Config(
    @JsonProperty("user_id") String userId,
    @JsonProperty("user_agent") String userAgent,
    @JsonProperty("x_bc") String xBc,
    @JsonProperty("sess") String sessionCookie,
    @JsonProperty("app_token") String appToken) {}
