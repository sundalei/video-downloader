package com.sundalei.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record DynamicRules(
        @JsonProperty("static_param") String staticParam,
        @JsonProperty("checksum_indexes") List<Integer> checksumIndexes,
        @JsonProperty("checksum_constant") Integer checksumConstant,
        @JsonProperty("format") String format) {
}
