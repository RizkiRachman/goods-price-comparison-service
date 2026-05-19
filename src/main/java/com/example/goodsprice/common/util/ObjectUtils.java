package com.example.goodsprice.common.util;

import java.util.Objects;
import java.util.function.Function;

public final class ObjectUtils {

  private ObjectUtils() {}

  public static <T> T defaultIfNull(T obj, T fallback) {
    return Objects.nonNull(obj) ? obj : fallback;
  }

  public static <T, R> R getOrNull(T obj, Function<T, R> getter) {
    if (Objects.isNull(obj)) return null;
    try {
      return getter.apply(obj);
    } catch (Exception e) {
      return null;
    }
  }

  public static <T, R> R getOrDefault(T obj, Function<T, R> getter, R defaultValue) {
    if (Objects.isNull(obj)) return defaultValue;
    var value = getter.apply(obj);
    return Objects.nonNull(value) ? value : defaultValue;
  }
}
