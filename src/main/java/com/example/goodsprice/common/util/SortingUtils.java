package com.example.goodsprice.common.util;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class SortingUtils {

  private SortingUtils() {}

  public static <T> List<T> sort(List<T> list, Comparator<T> comparator, String direction) {
    if (Objects.isNull(comparator)) {
      return list;
    }
    return isDescending(direction)
        ? list.stream().sorted(comparator.reversed()).toList()
        : list.stream().sorted(comparator).toList();
  }

  public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
      Function<T, U> keyExtractor) {
    return Comparator.comparing(keyExtractor, Comparator.nullsLast(Comparator.naturalOrder()));
  }

  public static <T> Comparator<T> comparingString(Function<T, String> keyExtractor) {
    return Comparator.comparing(keyExtractor, Comparator.nullsLast(String::compareTo));
  }

  public static boolean isDescending(String direction) {
    return "desc".equalsIgnoreCase(direction);
  }

  public static boolean isAscending(String direction) {
    return Objects.isNull(direction) || direction.isBlank() || "asc".equalsIgnoreCase(direction);
  }
}
