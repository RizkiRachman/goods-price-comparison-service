package com.example.goodsprice.common.web;

import static com.example.goodsprice.common.util.PaginationUtils.resolvePage;
import static com.example.goodsprice.common.util.PaginationUtils.resolveSize;
import static com.example.goodsprice.common.util.PaginationUtils.resolveSortBy;
import static com.example.goodsprice.common.util.PaginationUtils.resolveSortOrder;

import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.common.constant.AppConstants;
import com.example.goodsprice.common.util.ObjectUtils;

/**
 * Utility base for web adapters reducing pagination and response boilerplate. Not fully generic due
 * to varying OpenAPI DTO types — extend for shared pagination pattern.
 */
public class AbstractCrudWebAdapter {

  /**
   * Resolves a standard pagination parameter tuple from nullable API inputs. Default sort order is
   * "asc".
   *
   * @param page nullable page number
   * @param pageSize nullable page size
   * @param sortBy nullable sort field
   * @param sortOrder nullable sort direction
   * @param defaultSort fallback sort field
   * @return resolved pagination parameters
   */
  protected PaginationParams resolvePagination(
      Integer page, Integer pageSize, String sortBy, String sortOrder, String defaultSort) {
    return new PaginationParams(
        resolvePage(page, 1),
        resolveSize(pageSize, AppConstants.DEFAULT_PAGE_SIZE),
        resolveSortBy(sortBy, defaultSort),
        resolveSortOrder(sortOrder, "asc"));
  }

  /**
   * Resolves a standard pagination parameter tuple from nullable API inputs with a custom default
   * sort order.
   *
   * @param page nullable page number
   * @param pageSize nullable page size
   * @param sortBy nullable sort field
   * @param sortOrder nullable sort direction
   * @param defaultSort fallback sort field
   * @param defaultSortOrder fallback sort direction (e.g. "asc" or "desc")
   * @return resolved pagination parameters
   */
  protected PaginationParams resolvePagination(
      Integer page,
      Integer pageSize,
      String sortBy,
      String sortOrder,
      String defaultSort,
      String defaultSortOrder) {
    return new PaginationParams(
        resolvePage(page, 1),
        resolveSize(pageSize, AppConstants.DEFAULT_PAGE_SIZE),
        resolveSortBy(sortBy, defaultSort),
        resolveSortOrder(sortOrder, defaultSortOrder));
  }

  /**
   * Resolves EntityStatus to its string value or null.
   *
   * @param status nullable entity status
   * @return status string value, or null
   */
  protected String resolveStatus(EntityStatus status) {
    return ObjectUtils.getOrNull(status, EntityStatus::getValue);
  }

  /**
   * Pagination parameter value object.
   *
   * @param page resolved page number
   * @param size resolved page size
   * @param sortBy resolved sort field
   * @param sortOrder resolved sort direction
   */
  protected record PaginationParams(int page, int size, String sortBy, String sortOrder) {}
}
