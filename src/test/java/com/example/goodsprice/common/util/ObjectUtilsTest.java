package com.example.goodsprice.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ObjectUtilsTest {

  @Test
  void shouldReturnObjWhenNonNull() {
    assertEquals("hello", ObjectUtils.defaultIfNull("hello", "fallback"));
  }

  @Test
  void shouldReturnFallbackWhenObjIsNull() {
    assertEquals("fallback", ObjectUtils.defaultIfNull(null, "fallback"));
  }

  @Test
  void shouldReturnNullWhenBothAreNull() {
    assertNull(ObjectUtils.defaultIfNull(null, null));
  }

  @Test
  void shouldGetOrNullWhenObjIsNonNull() {
    assertEquals("VALUE", ObjectUtils.getOrNull("VALUE", s -> s));
  }

  @Test
  void shouldReturnNullWhenObjIsNull() {
    assertNull(ObjectUtils.getOrNull(null, s -> s));
  }

  @Test
  void shouldReturnNullWhenGetterThrows() {
    assertNull(ObjectUtils.getOrNull("test", s -> { throw new RuntimeException(); }));
  }

  @Test
  void shouldReturnDefaultWhenObjIsNull() {
    assertEquals("default", ObjectUtils.getOrDefault(null, s -> s, "default"));
  }

  @Test
  void shouldReturnDefaultWhenGetterReturnsNull() {
    assertEquals("default", ObjectUtils.getOrDefault("test", s -> null, "default"));
  }

  @Test
  void shouldReturnValueWhenGetterReturnsNonNull() {
    assertEquals("VALUE", ObjectUtils.getOrDefault("VALUE", s -> s, "default"));
  }
}
