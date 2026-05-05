package com.example.goodsprice.common.dto;

import com.example.goodsprice.api.model.Pagination;
import java.util.List;

public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last) {

  public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
    var totalPages = (int) Math.ceil((double) totalElements / size);
    return new PageResponse<>(
        content, page, size, totalElements, totalPages, page == 0, page >= totalPages - 1);
  }

  public Pagination toPagination() {
    var pagination = new Pagination();
    pagination.setPage(page);
    pagination.setPageSize(size);
    pagination.setTotalItems((int) totalElements);
    pagination.setTotalPages(totalPages);
    pagination.setHasNext(!last);
    pagination.setHasPrevious(!first);
    return pagination;
  }
}
