package com.example.goodsprice.common.util;

import java.util.Objects;

public final class NumberUtils {

  private NumberUtils() {}

  public static Double toDouble(Object value) {
    if (Objects.isNull(value)) return null;
    if (value instanceof Number n) return n.doubleValue();
    try {
      return Double.parseDouble(value.toString());
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
