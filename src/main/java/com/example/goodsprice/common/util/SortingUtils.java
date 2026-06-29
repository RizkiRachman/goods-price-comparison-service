package com.example.goodsprice.common.util;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class SortingUtils {

  private SortingUtils() {}

  /**
   * Sorts a list with the given comparator and direction.
   *
   * @deprecated Use {@code list.stream().sorted(comparator)} directly instead. Only used in tests.
   */
  @Deprecated(forRemoval = true, since = "1.0.0")
  public static <T> List<T> sort(List<T> list, Comparator<T> comparator, String direction) {
    if (Objects.isNull(comparator)) {
      return list;
    }
    return isDescending(direction)
        ? list.stream().sorted(comparator.reversed()).toList()
        : list.stream().sorted(comparator).toList();
  }

  /**
   * Creates a comparator for a comparable key extractor with nulls-last.
   *
   * @deprecated Use {@code Comparator.comparing(keyExtractor,
   *     Comparator.nullsLast(Comparator.naturalOrder()))} directly instead. No production callers
   *     outside tests.
   */
  @Deprecated(forRemoval = true, since = "1.0.0")
  public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
      Function<T, U> keyExtractor) {
    return Comparator.comparing(keyExtractor, Comparator.nullsLast(Comparator.naturalOrder()));
  }

  /**
   * Creates a string comparator with nulls-last.
   *
   * @deprecated Use {@code Comparator.comparing(keyExtractor,
   *     Comparator.nullsLast(String::compareTo))} directly instead.
   */
  @Deprecated(forRemoval = true, since = "1.0.0")
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
