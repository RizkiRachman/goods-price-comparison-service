package com.example.goodsprice.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NumberUtilsTest {

  @Test
  @DisplayName("Should return null for null input")
  void shouldReturnNullForNull() {
    assertNull(NumberUtils.toDouble(null));
  }

  @Test
  @DisplayName("Should return double from Double instance")
  void shouldReturnDoubleFromDouble() {
    assertEquals(3.14, NumberUtils.toDouble(3.14), 0.0001);
  }

  @Test
  @DisplayName("Should return double from Integer instance")
  void shouldReturnDoubleFromInteger() {
    assertEquals(42.0, NumberUtils.toDouble(42), 0.0001);
  }

  @Test
  @DisplayName("Should return double from Long instance")
  void shouldReturnDoubleFromLong() {
    assertEquals(100L, NumberUtils.toDouble(100L), 0.0001);
  }

  @Test
  @DisplayName("Should return double from Float instance")
  void shouldReturnDoubleFromFloat() {
    assertEquals(2.5f, NumberUtils.toDouble(2.5f), 0.0001);
  }

  @Test
  @DisplayName("Should parse valid numeric string")
  void shouldParseValidNumericString() {
    assertEquals(99.99, NumberUtils.toDouble("99.99"), 0.0001);
  }

  @Test
  @DisplayName("Should parse integer string")
  void shouldParseIntegerString() {
    assertEquals(50.0, NumberUtils.toDouble("50"), 0.0001);
  }

  @Test
  @DisplayName("Should return null for invalid string")
  void shouldReturnNullForInvalidString() {
    assertNull(NumberUtils.toDouble("not-a-number"));
  }

  @Test
  @DisplayName("Should return null for empty string")
  void shouldReturnNullForEmptyString() {
    assertNull(NumberUtils.toDouble(""));
  }
}
