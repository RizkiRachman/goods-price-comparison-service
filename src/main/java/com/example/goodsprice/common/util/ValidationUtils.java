package com.example.goodsprice.common.util;

import java.util.Objects;

public final class ValidationUtils {

  private ValidationUtils() {}

  public static <T> T requireNonNull(T obj, String fieldName) {
    if (Objects.isNull(obj)) {
      throw new IllegalArgumentException("%s must not be null".formatted(fieldName));
    }
    return obj;
  }

  public static void requirePositive(Number value, String name) {
    if (Objects.isNull(value) || value.doubleValue() <= 0) {
      throw new IllegalArgumentException("%s must be positive".formatted(name));
    }
  }

  public static void requireNotBlank(String value, String name) {
    if (Objects.isNull(value) || value.isBlank()) {
      throw new IllegalArgumentException("%s must not be blank".formatted(name));
    }
  }
}
