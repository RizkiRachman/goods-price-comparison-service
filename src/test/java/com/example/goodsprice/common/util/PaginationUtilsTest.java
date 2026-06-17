package com.example.goodsprice.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaginationUtilsTest {

  @Test
  void shouldPaginateFullList() {
    var items = List.of("a", "b", "c", "d");
    var result = PaginationUtils.paginate(items, 0, 2);
    assertEquals(4, result.totalElements());
    assertEquals(List.of("a", "b"), result.content());
  }

  @Test
  void shouldPaginateSecondPage() {
    var items = List.of("a", "b", "c", "d");
    var result = PaginationUtils.paginate(items, 1, 2);
    assertEquals(List.of("c", "d"), result.content());
  }

  @Test
  void shouldReturnEmptyWhenPageOutOfBounds() {
    var items = List.of("a", "b");
    var result = PaginationUtils.paginate(items, 5, 2);
    assertEquals(0, result.content().size());
    assertEquals(2, result.totalElements());
  }

  @Test
  void shouldReturnEmptyWhenListIsNull() {
    var result = PaginationUtils.paginate(null, 0, 10);
    assertEquals(0, result.content().size());
  }

  @Test
  void shouldReturnEmptyWhenListIsEmpty() {
    var result = PaginationUtils.paginate(List.of(), 0, 10);
    assertEquals(0, result.content().size());
  }

  @Test
  void shouldNormalizePageReturnsZeroWhenNull() {
    assertEquals(0, PaginationUtils.normalizePage(null));
  }

  @Test
  void shouldNormalizePageReturnsZeroWhenNegative() {
    assertEquals(0, PaginationUtils.normalizePage(-1));
  }

  @Test
  void shouldNormalizePageReturnsSameWhenValid() {
    assertEquals(3, PaginationUtils.normalizePage(3));
  }

  @Test
  void shouldNormalizeSizeReturnsDefaultWhenNull() {
    assertEquals(20, PaginationUtils.normalizeSize(null));
  }

  @Test
  void shouldNormalizeSizeReturnsDefaultWhenZero() {
    assertEquals(20, PaginationUtils.normalizeSize(0));
  }

  @Test
  void shouldNormalizeSizeClampsToMax() {
    assertEquals(100, PaginationUtils.normalizeSize(200));
  }

  @Test
  void shouldNormalizeSizeReturnsSameWhenValid() {
    assertEquals(15, PaginationUtils.normalizeSize(15));
  }

  @Test
  void shouldResolvePageReturnsDefaultWhenNull() {
    assertEquals(1, PaginationUtils.resolvePage(null, 1));
  }

  @Test
  void shouldResolvePageReturnsDefaultWhenNegative() {
    assertEquals(0, PaginationUtils.resolvePage(-5, 0));
  }

  @Test
  void shouldResolvePageReturnsSameWhenValid() {
    assertEquals(2, PaginationUtils.resolvePage(2, 1));
  }

  @Test
  void shouldResolveSizeReturnsDefaultWhenNull() {
    assertEquals(10, PaginationUtils.resolveSize(null, 10));
  }

  @Test
  void shouldResolveSizeReturnsDefaultWhenZero() {
    assertEquals(10, PaginationUtils.resolveSize(0, 10));
  }

  @Test
  void shouldResolveSizeClampsToMax() {
    assertEquals(100, PaginationUtils.resolveSize(200, 10));
  }

  @Test
  void shouldResolveSizeReturnsSameWhenValid() {
    assertEquals(25, PaginationUtils.resolveSize(25, 10));
  }

  @Test
  void shouldResolveSortByReturnsDefaultWhenNull() {
    assertEquals("name", PaginationUtils.resolveSortBy(null, "name"));
  }

  @Test
  void shouldResolveSortByReturnsDefaultWhenBlank() {
    assertEquals("name", PaginationUtils.resolveSortBy("  ", "name"));
  }

  @Test
  void shouldResolveSortByReturnsSameWhenValid() {
    assertEquals("price", PaginationUtils.resolveSortBy("price", "name"));
  }

  @Test
  void shouldResolveSortOrderReturnsDefaultWhenNull() {
    assertEquals("asc", PaginationUtils.resolveSortOrder(null, "asc"));
  }

  @Test
  void shouldResolveSortOrderReturnsDefaultWhenBlank() {
    assertEquals("desc", PaginationUtils.resolveSortOrder("", "desc"));
  }

  @Test
  void shouldResolveSortOrderReturnsSameWhenValid() {
    assertEquals("desc", PaginationUtils.resolveSortOrder("desc", "asc"));
  }

  @Test
  @DisplayName("Should resolve sort ascending when direction is asc")
  void shouldResolveSortAscending() {
    var sort = PaginationUtils.resolveSort("name", "asc", "id");
    assertTrue(sort.getOrderFor("name").isAscending());
  }

  @Test
  @DisplayName("Should resolve sort descending when direction is desc")
  void shouldResolveSortDescending() {
    var sort = PaginationUtils.resolveSort("name", "desc", "id");
    assertTrue(sort.getOrderFor("name").isDescending());
  }

  @Test
  @DisplayName("Should resolve sort with default field when sortBy is null")
  void shouldResolveSortWithDefaultField() {
    var sort = PaginationUtils.resolveSort(null, "asc", "id");
    assertTrue(sort.getOrderFor("id").isAscending());
  }

  @Test
  @DisplayName("Should resolve sort with default field when sortBy is blank")
  void shouldResolveSortWithDefaultFieldWhenSortByIsBlank() {
    var sort = PaginationUtils.resolveSort("", "desc", "createdAt");
    assertTrue(sort.getOrderFor("createdAt").isDescending());
  }
}
