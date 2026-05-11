package com.example.goodsprice.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ObjectUtilsTest {

  @Test
  @DisplayName("Should return object when non-null")
  void shouldReturnObjectWhenNonNull() {
    assertEquals("hello", ObjectUtils.defaultIfNull("hello", "fallback"));
  }

  @Test
  @DisplayName("Should return fallback when object is null")
  void shouldReturnFallbackWhenNull() {
    assertEquals("fallback", ObjectUtils.defaultIfNull(null, "fallback"));
  }

  @Test
  @DisplayName("Should return value from getter when object is non-null")
  void shouldReturnValueFromGetterWhenNonNull() {
    var result = ObjectUtils.getOrNull("hello", String::length);
    assertEquals(5, result);
  }

  @Test
  @DisplayName("Should return null when object is null")
  void shouldReturnNullWhenObjectIsNull() {
    assertNull(ObjectUtils.getOrNull(null, String::length));
  }

  @Test
  @DisplayName("Should return null when getter returns null")
  void shouldReturnNullWhenGetterReturnsNull() {
    var result = ObjectUtils.getOrNull("hello", s -> null);
    assertNull(result);
  }

  @Test
  @DisplayName("Should return value when object and getter are non-null")
  void shouldReturnValueFromGetter() {
    var result = ObjectUtils.getOrDefault("hello", String::length, -1);
    assertEquals(5, result);
  }

  @Test
  @DisplayName("Should return default when object is null")
  void shouldReturnDefaultWhenObjectIsNull() {
    var result = ObjectUtils.getOrDefault(null, String::length, -1);
    assertEquals(-1, result);
  }

  @Test
  @DisplayName("Should return default when getter returns null")
  void shouldReturnDefaultWhenGetterReturnsNull() {
    var result = ObjectUtils.getOrDefault("hello", s -> null, -1);
    assertEquals(-1, result);
  }

  @Test
  @DisplayName("Should handle Integer objects with defaultIfNull")
  void shouldHandleIntegerObjects() {
    assertEquals(42, ObjectUtils.defaultIfNull(42, 0));
    assertEquals(0, ObjectUtils.defaultIfNull(null, 0));
  }
}
