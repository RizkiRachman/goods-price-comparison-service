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
}
