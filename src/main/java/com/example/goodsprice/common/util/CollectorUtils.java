package com.example.goodsprice.common.util;

import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class CollectorUtils {
  private CollectorUtils() {}

  public static <T, K> Collector<T, ?, Map<K, T>> toIdentityMap(
      Function<? super T, ? extends K> keyExtractor) {
    return Collectors.toMap(keyExtractor, Function.identity());
  }

  public static <T, K> Collector<T, ?, Map<K, T>> toIdentityMap(
      Function<? super T, ? extends K> keyExtractor, BinaryOperator<T> mergeFunction) {
    return Collectors.toMap(keyExtractor, Function.identity(), mergeFunction);
  }
}
