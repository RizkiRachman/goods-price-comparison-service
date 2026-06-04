package com.example.goodsprice.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CorsPropertiesTest {

  @Test
  void shouldHaveDefaultValues() {
    var props = new CorsProperties();

    assertEquals(List.of("http://localhost:3000"), props.getAllowedOriginPatterns());
    assertEquals(
        List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"), props.getAllowedMethods());
    assertEquals(List.of("*"), props.getAllowedHeaders());
    assertTrue(props.isAllowCredentials());
  }

  @Test
  void shouldAllowSettingCustomValues() {
    var props = new CorsProperties();
    props.setAllowedOriginPatterns(List.of("https://example.com"));
    props.setAllowedMethods(List.of("GET"));
    props.setAllowedHeaders(List.of("Authorization"));
    props.setAllowCredentials(false);

    assertEquals(List.of("https://example.com"), props.getAllowedOriginPatterns());
    assertEquals(List.of("GET"), props.getAllowedMethods());
    assertEquals(List.of("Authorization"), props.getAllowedHeaders());
    assertFalse(props.isAllowCredentials());
  }
}
