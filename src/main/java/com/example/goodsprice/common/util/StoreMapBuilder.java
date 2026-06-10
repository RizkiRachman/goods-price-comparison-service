package com.example.goodsprice.common.util;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class StoreMapBuilder {
  private StoreMapBuilder() {}

  public static <T, K> Map<K, T> buildFromIds(
      List<K> ids, Function<List<K>, List<T>> fetcher, Function<T, K> idExtractor) {
    if (ids.isEmpty()) return Map.of();
    return fetcher.apply(ids).stream().collect(Collectors.toMap(idExtractor, Function.identity()));
  }

  public static <T> Map<Long, T> buildFromDomainIds(
      List<? extends T> domains,
      Function<T, Long> idExtractor,
      Function<List<Long>, List<T>> fetcher) {
    var ids = domains.stream().map(idExtractor).filter(Objects::nonNull).distinct().toList();
    if (ids.isEmpty()) return Map.of();
    return fetcher.apply(ids).stream().collect(Collectors.toMap(idExtractor, Function.identity()));
  }
}
