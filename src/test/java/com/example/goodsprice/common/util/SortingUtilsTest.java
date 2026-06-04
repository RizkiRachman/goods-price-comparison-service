package com.example.goodsprice.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class SortingUtilsTest {

  @Test
  void shouldSortAscending() {
    var result = SortingUtils.sort(List.of(3, 1, 2), Integer::compareTo, "asc");
    assertEquals(List.of(1, 2, 3), result);
  }

  @Test
  void shouldSortDescending() {
    var result = SortingUtils.sort(List.of(1, 2, 3), Integer::compareTo, "desc");
    assertEquals(List.of(3, 2, 1), result);
  }

  @Test
  void shouldSortAscendingByDefault() {
    var result = SortingUtils.sort(List.of(3, 1, 2), Integer::compareTo, null);
    assertEquals(List.of(1, 2, 3), result);
  }

  @Test
  void shouldReturnOriginalListWhenComparatorIsNull() {
    var list = List.of(3, 1, 2);
    var result = SortingUtils.sort(list, null, "asc");
    assertEquals(list, result);
  }

  @Test
  void shouldCompareByKeyExtractor() {
    var comparator = SortingUtils.comparing(String::length);
    assertNotNull(comparator);
    assertEquals(0, comparator.compare("aa", "aa"));
  }

  @Test
  void shouldCompareString() {
    var comparator = SortingUtils.comparingString(String::toString);
    assertNotNull(comparator);
    assertEquals(0, comparator.compare("a", "a"));
  }

  @Test
  void shouldBeDescendingForDescInput() {
    assertTrue(SortingUtils.isDescending("desc"));
    assertTrue(SortingUtils.isDescending("DESC"));
  }

  @Test
  void shouldNotBeDescendingForAscInput() {
    assertFalse(SortingUtils.isDescending("asc"));
    assertFalse(SortingUtils.isDescending("ASC"));
  }

  @Test
  void shouldBeAscendingForNullDirection() {
    assertTrue(SortingUtils.isAscending(null));
  }

  @Test
  void shouldBeAscendingForBlankDirection() {
    assertTrue(SortingUtils.isAscending("  "));
  }

  @Test
  void shouldBeAscendingForAscInput() {
    assertTrue(SortingUtils.isAscending("asc"));
  }

  @Test
  void shouldNotBeAscendingForDescInput() {
    assertFalse(SortingUtils.isAscending("desc"));
  }
}
