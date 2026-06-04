package com.example.goodsprice.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class NumberUtilsTest {

  @Test
  void shouldConvertNullToNull() {
    assertNull(NumberUtils.toDouble(null));
  }

  @Test
  void shouldConvertIntegerToDouble() {
    assertEquals(42.0, NumberUtils.toDouble(42));
  }

  @Test
  void shouldConvertDoubleToDouble() {
    assertEquals(3.14, NumberUtils.toDouble(3.14));
  }

  @Test
  void shouldConvertValidStringToDouble() {
    assertEquals(12.5, NumberUtils.toDouble("12.5"));
  }

  @Test
  void shouldReturnNullForInvalidString() {
    assertNull(NumberUtils.toDouble("not-a-number"));
  }
}
