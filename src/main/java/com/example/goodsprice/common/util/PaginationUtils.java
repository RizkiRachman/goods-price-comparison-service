package com.example.goodsprice.common.util;

import com.example.goodsprice.common.dto.PageResponse;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

  public static int normalizePage(Integer page) {
    return Objects.nonNull(page) && page >= 0 ? page : 0;
  }

  public static int normalizeSize(Integer size, int defaultSize, int maxSize) {
    if (Objects.isNull(size) || size <= 0) {
      return defaultSize;
    }
    return Math.min(size, maxSize);
  }

  public static int normalizeSize(Integer size) {
    return normalizeSize(size, 20, 100);
  }
}
