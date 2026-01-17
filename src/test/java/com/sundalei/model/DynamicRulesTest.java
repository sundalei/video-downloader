package com.sundalei.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.ObjectMapper;

class DynamicRulesTest {

  @Test
  void testFormatStringCompatibility() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    DynamicRules rules =
        mapper.readValue(
            new ClassPathResource("dynamic_rules.json").getInputStream(), DynamicRules.class);

    String format = rules.format();
    assertThat(format).isNotNull();

    // Verify it works with String.format with expected arguments (String, Integer)
    String formatted = String.format(format, "testString", 123);

    assertThat(formatted).startsWith("51892:testString:7b:69406376");
  }
}
