package com.example.goodsprice.common.util;

import com.example.goodsprice.common.dto.PageResponse;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Generic utility class for pagination operations. Provides reusable methods for paginating lists.
 */
public final class PaginationUtils {

  private PaginationUtils() {}

  /**
   * Paginates a list based on page number and size.
   *
   * @param list the list to paginate
   * @param page the page number (0-indexed)
   * @param size the page size
   * @return a PageResponse containing the paginated content
   */
  public static <T> PageResponse<T> paginate(List<T> list, int page, int size) {
    if (Objects.isNull(list) || list.isEmpty()) {
      return PageResponse.of(Collections.emptyList(), page, size, 0);
    }

    int totalElements = list.size();
    int start = page * size;

    if (start >= totalElements) {
      return PageResponse.of(Collections.emptyList(), page, size, totalElements);
    }

    int end = Math.min(start + size, totalElements);
    List<T> content = list.subList(start, end);

    return PageResponse.of(content, page, size, totalElements);
  }

  /**
   * Validates and normalizes page number (ensures non-negative).
   *
   * @param page the page number
   * @return normalized page number (minimum 0)
   */
  public static int normalizePage(Integer page) {
    return Objects.nonNull(page) && page >= 0 ? page : 0;
  }

  /**
   * Validates and normalizes page size (ensures positive, with max limit).
   *
   * @param size the page size
   * @param defaultSize the default size if null or invalid
   * @param maxSize the maximum allowed size
   * @return normalized page size
   */
  public static int normalizeSize(Integer size, int defaultSize, int maxSize) {
    if (Objects.isNull(size) || size <= 0) {
      return defaultSize;
    }
    return Math.min(size, maxSize);
  }

  /**
   * Validates and normalizes page size with default defaults.
   *
   * @param size the page size
   * @return normalized page size (between 1 and 100, default 20)
   */
  public static int normalizeSize(Integer size) {
    return normalizeSize(size, 20, 100);
  }
}
