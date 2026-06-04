package com.example.goodsprice.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;

class JsonNullableUtilsTest {

  @Test
  void shouldResolveValueWhenPresent() {
    var result = JsonNullableUtils.resolveNullable(JsonNullable.of("hello"));
    assertEquals("hello", result);
  }

  @Test
  void shouldReturnNullWhenNullableIsNull() {
    assertNull(JsonNullableUtils.resolveNullable(null));
  }
}
