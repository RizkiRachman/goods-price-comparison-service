package com.example.goodsprice.common.util;

import java.util.Objects;
import java.util.function.Function;

/**
 * Stable utility for null-safe object access.
 *
 * <p><strong>This class is sealed — do not add new methods here.</strong> New utility methods
 * belong in their own class under {@code common/util/}.
 */
public final class ObjectUtils {

  private ObjectUtils() {}

  public static <T> T defaultIfNull(T obj, T fallback) {
    return Objects.nonNull(obj) ? obj : fallback;
  }

  public static <T, R> R getOrNull(T obj, Function<T, R> getter) {
    if (Objects.isNull(obj)) return null;
    try {
      return getter.apply(obj);
    } catch (RuntimeException e) {
      return null;
    }
  }

  public static <T, R> R getOrDefault(T obj, Function<T, R> getter, R defaultValue) {
    if (Objects.isNull(obj)) return defaultValue;
    var value = getter.apply(obj);
    return Objects.nonNull(value) ? value : defaultValue;
  }
}
