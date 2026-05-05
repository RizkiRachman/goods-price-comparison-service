package com.example.goodsprice.common.util;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Generic utility class for sorting operations. Provides reusable methods for sorting lists with
 * various strategies.
 */
public final class SortingUtils {

  private SortingUtils() {}

  /**
   * Sorts a list using the provided comparator and direction.
   *
   * @param list the list to sort
   * @param comparator the comparator to use for sorting
   * @param direction the sort direction ("asc" or "desc", case-insensitive)
   * @return the sorted list, or original list if comparator is null
   */
  public static <T> List<T> sort(List<T> list, Comparator<T> comparator, String direction) {
    if (Objects.isNull(comparator)) {
      return list;
    }
    return isDescending(direction)
        ? list.stream().sorted(comparator.reversed()).toList()
        : list.stream().sorted(comparator).toList();
  }

  /**
   * Creates a null-safe comparator using the provided key extractor. Null values are sorted last.
   *
   * @param keyExtractor function to extract the comparable key
   * @return a comparator that handles null values
   */
  public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
      Function<T, U> keyExtractor) {
    return Comparator.comparing(keyExtractor, Comparator.nullsLast(Comparator.naturalOrder()));
  }

  /**
   * Creates a null-safe comparator for String keys. Null values are sorted last.
   *
   * @param keyExtractor function to extract the string key
   * @return a comparator that handles null values
   */
  public static <T> Comparator<T> comparingString(Function<T, String> keyExtractor) {
    return Comparator.comparing(keyExtractor, Comparator.nullsLast(String::compareTo));
  }

  /**
   * Checks if the direction indicates descending order.
   *
   * @param direction the sort direction string
   * @return true if direction is "desc" (case-insensitive)
   */
  public static boolean isDescending(String direction) {
    return "desc".equalsIgnoreCase(direction);
  }

  /**
   * Checks if the direction indicates ascending order (or default).
   *
   * @param direction the sort direction string
   * @return true if direction is null, blank, or "asc" (case-insensitive)
   */
  public static boolean isAscending(String direction) {
    return Objects.isNull(direction) || direction.isBlank() || "asc".equalsIgnoreCase(direction);
  }
}
