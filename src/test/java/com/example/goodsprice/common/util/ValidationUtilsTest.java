package com.example.goodsprice.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ValidationUtilsTest {

  @Test
  @DisplayName("Should pass for non-null object")
  void shouldPassForNonNullObject() {
    var result = ValidationUtils.requireNonNull("hello", "name");
    assertEquals("hello", result);
  }

  @Test
  @DisplayName("Should throw for null object")
  void shouldThrowForNullObject() {
    var ex =
        assertThrows(
            IllegalArgumentException.class, () -> ValidationUtils.requireNonNull(null, "name"));
    assertEquals("name must not be null", ex.getMessage());
  }

  @Test
  @DisplayName("Should pass for non-blank string")
  void shouldPassForNonBlankString() {
    var result = ValidationUtils.requireNonBlank("hello", "name");
    assertEquals("hello", result);
  }

  @Test
  @DisplayName("Should throw for null string")
  void shouldThrowForNullString() {
    var ex =
        assertThrows(
            IllegalArgumentException.class, () -> ValidationUtils.requireNonBlank(null, "name"));
    assertEquals("name must not be blank", ex.getMessage());
  }

  @Test
  @DisplayName("Should throw for blank string")
  void shouldThrowForBlankString() {
    var ex =
        assertThrows(
            IllegalArgumentException.class, () -> ValidationUtils.requireNonBlank("  ", "name"));
    assertEquals("name must not be blank", ex.getMessage());
  }

  @Test
  @DisplayName("Should throw for empty string")
  void shouldThrowForEmptyString() {
    var ex =
        assertThrows(
            IllegalArgumentException.class, () -> ValidationUtils.requireNonBlank("", "name"));
    assertEquals("name must not be blank", ex.getMessage());
  }
}
