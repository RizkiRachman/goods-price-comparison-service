package com.example.goodsprice.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ValidationUtilsTest {

  @Test
  void shouldRequireNonNullReturnsObjWhenNonNull() {
    assertEquals("hello", ValidationUtils.requireNonNull("hello", "name"));
  }

  @Test
  void shouldRequireNonNullThrowsWhenNull() {
    var ex =
        assertThrows(
            IllegalArgumentException.class, () -> ValidationUtils.requireNonNull(null, "name"));
    assertEquals("name must not be null", ex.getMessage());
  }

  @Test
  void shouldRequireNotBlankPassesWhenNotBlank() {
    ValidationUtils.requireNotBlank("hello", "name");
  }

  @Test
  void shouldRequireNotBlankThrowsWhenNull() {
    var ex =
        assertThrows(
            IllegalArgumentException.class, () -> ValidationUtils.requireNotBlank(null, "name"));
    assertEquals("name must not be blank", ex.getMessage());
  }

  @Test
  void shouldRequireNotBlankThrowsWhenBlank() {
    var ex =
        assertThrows(
            IllegalArgumentException.class, () -> ValidationUtils.requireNotBlank("  ", "name"));
    assertEquals("name must not be blank", ex.getMessage());
  }

  @Test
  void shouldRequireNotBlankThrowsWhenEmpty() {
    var ex =
        assertThrows(
            IllegalArgumentException.class, () -> ValidationUtils.requireNotBlank("", "name"));
    assertEquals("name must not be blank", ex.getMessage());
  }

  @Test
  void shouldRequirePositivePassesWhenPositive() {
    ValidationUtils.requirePositive(5, "value");
  }

  @Test
  void shouldRequirePositiveThrowsWhenNull() {
    var ex =
        assertThrows(
            IllegalArgumentException.class, () -> ValidationUtils.requirePositive(null, "value"));
    assertEquals("value must be positive", ex.getMessage());
  }

  @Test
  void shouldRequirePositiveThrowsWhenZero() {
    var ex =
        assertThrows(
            IllegalArgumentException.class, () -> ValidationUtils.requirePositive(0, "value"));
    assertEquals("value must be positive", ex.getMessage());
  }

  @Test
  void shouldRequirePositiveThrowsWhenNegative() {
    var ex =
        assertThrows(
            IllegalArgumentException.class, () -> ValidationUtils.requirePositive(-1, "value"));
    assertEquals("value must be positive", ex.getMessage());
  }
}
