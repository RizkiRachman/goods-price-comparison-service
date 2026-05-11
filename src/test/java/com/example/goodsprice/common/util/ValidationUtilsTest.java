package com.example.goodsprice.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ValidationUtilsTest {

  @Test
  @DisplayName("Should return the object when non-null")
  void shouldReturnObjectWhenNonNull() {
    var obj = new Object();
    assertSame(obj, ValidationUtils.requireNonNull(obj, "testField"));
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when object is null")
  void shouldThrowWhenNull() {
    var ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.requireNonNull(null, "testField"));
    assertEquals("testField must not be null", ex.getMessage());
  }

  @Test
  @DisplayName("Should include field name in exception message")
  void shouldIncludeFieldNameInMessage() {
    var ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> ValidationUtils.requireNonNull(null, "productName"));
    assertEquals("productName must not be null", ex.getMessage());
  }

  @Test
  @DisplayName("Should return String value unmodified")
  void shouldReturnStringValue() {
    var result = ValidationUtils.requireNonNull("hello", "greeting");
    assertEquals("hello", result);
  }
}
