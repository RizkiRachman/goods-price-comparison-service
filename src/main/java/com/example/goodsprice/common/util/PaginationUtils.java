package com.example.goodsprice.common.util;

import com.example.goodsprice.common.constant.AppConstants;
import com.example.goodsprice.common.dto.PageResponse;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Sort;

public final class PaginationUtils {

  private PaginationUtils() {}

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

  @Deprecated(forRemoval = true, since = "1.0.0")
  public static int normalizePage(Integer page) {
    return resolvePage(page, 0);
  }

  @Deprecated(forRemoval = true, since = "1.0.0")
  public static int normalizeSize(Integer size, int defaultSize, int maxSize) {
    var clamped = resolveSize(size, defaultSize);
    return clamped > maxSize ? maxSize : clamped;
  }

  @Deprecated(forRemoval = true, since = "1.0.0")
  public static int normalizeSize(Integer size) {
    return resolveSize(size, 20);
  }

  /**
   * Resolves a nullable page parameter to a non-null default.
   *
   * @param page nullable page number
   * @param defaultPage default if null (typically 0 or 1)
   * @return normalized page number
   */
  public static int resolvePage(Integer page, int defaultPage) {
    return Objects.nonNull(page) && page >= 0 ? page : defaultPage;
  }

  /**
   * Resolves a nullable page size parameter to a non-null default.
   *
   * @param size nullable page size
   * @param defaultSize default if null or invalid
   * @return normalized page size (clamped to reasonable max)
   */
  public static int resolveSize(Integer size, int defaultSize) {
    if (Objects.isNull(size) || size <= 0) return defaultSize;
    return Math.min(size, AppConstants.MAX_PAGE_SIZE);
  }

  /**
   * Resolves a nullable sort-by parameter to a default.
   *
   * @param sortBy nullable sort field
   * @param defaultSortBy fallback value
   * @return non-null sort field
   */
  public static String resolveSortBy(String sortBy, String defaultSortBy) {
    return Objects.nonNull(sortBy) && !sortBy.isBlank() ? sortBy : defaultSortBy;
  }

  /**
   * Resolves a nullable sort direction parameter to a default.
   *
   * @param sortOrder nullable sort direction
   * @param defaultSortOrder fallback value (e.g. "asc" or "desc")
   * @return non-null sort direction
   */
  public static String resolveSortOrder(String sortOrder, String defaultSortOrder) {
    return Objects.nonNull(sortOrder) && !sortOrder.isBlank() ? sortOrder : defaultSortOrder;
  }

  /**
   * Resolves sort parameters into a Spring Data Sort object.
   *
   * @param sortBy sort field (nullable, falls back to defaultSortBy)
   * @param sortDirection sort direction ("desc" or anything else for asc)
   * @param defaultSortBy fallback sort field if sortBy is null/blank
   * @return configured Sort object
   */
  public static Sort resolveSort(String sortBy, String sortDirection, String defaultSortBy) {
    return Sort.by(
        "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC,
        resolveSortBy(sortBy, defaultSortBy));
  }
}
