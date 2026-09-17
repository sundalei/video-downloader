package com.sundalei.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class DynamicRulesTest {

  @Test
  void testFormatStringCompatibility() throws IOException {
    String format = "51892:%s:%x:69406376";
    assertThat(format).isNotNull();

    // Verify it works with String.format with expected arguments (String, Integer)
    String formatted = String.format(format, "testString", 123);

    assertThat(formatted).startsWith("51892:testString:7b:69406376");
  }
}
