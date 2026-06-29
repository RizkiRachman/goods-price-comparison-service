package com.example.goodsprice.common.web;

import static com.example.goodsprice.common.util.PaginationUtils.resolvePage;
import static com.example.goodsprice.common.util.PaginationUtils.resolveSize;
import static com.example.goodsprice.common.util.PaginationUtils.resolveSortBy;
import static com.example.goodsprice.common.util.PaginationUtils.resolveSortOrder;

import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Pagination;
import com.example.goodsprice.common.constant.AppConstants;
import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.util.ObjectUtils;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility base for web adapters reducing pagination and response boilerplate. Not fully generic due
 * to varying OpenAPI DTO types — extend for shared pagination pattern.
 */
public class AbstractCrudWebAdapter {

  /**
   * Resolves a standard pagination parameter tuple from nullable API inputs.
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
   * Builds a PageRequestDto from already-resolved pagination parameters.
   *
   * @param params resolved pagination parameters
   * @return page request DTO
   */
  protected PageRequestDto buildPageRequest(PaginationParams params) {
    return new PageRequestDto(params.page(), params.size(), params.sortBy(), params.sortOrder());
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

  protected record ListResponseData<R>(List<R> data, Pagination pagination) {
    protected ListResponseData {
      data = List.copyOf(data);
    }
  }

  protected <D, R> ListResponseData<R> buildListResponse(
      PageResponse<D> pageResponse, Function<D, R> mapper) {
    var data = pageResponse.content().stream().map(mapper).toList();
    return new ListResponseData<>(data, pageResponse.toPagination());
  }

  /**
   * @deprecated Use {@link #buildTypedListResponse(PageResponse, Function, Supplier)} instead.
   *     Removed in a future release — no remaining callers in the codebase.
   */
  @Deprecated(since = "1.0.0", forRemoval = true)
  protected <D, R, S> S buildCompleteListResponse(
      PageResponse<D> pageResponse,
      Function<D, R> mapper,
      BiFunction<List<R>, Pagination, S> responseFactory) {
    var dp = buildListResponse(pageResponse, mapper);
    return responseFactory.apply(dp.data(), dp.pagination());
  }

  /**
   * Generic list response builder that creates a typed paginated response.
   *
   * <p>Eliminates the repetitive response factory lambda in each web adapter where the only
   * variation is the concrete response class. Callers provide a {@link Supplier} for their specific
   * response type (e.g. {@code XxxListResponse::new}), and the method internally populates the
   * standard {@code setData} and {@code setPagination} fields via reflection, since all generated
   * OpenAPI {@code *ListResponse} DTOs share the same property shape without a common interface.
   *
   * @param pageResponse paginated domain data
   * @param mapper domain-to-API mapping function
   * @param responseFactory supplier for the specific response type
   * @param <D> domain type
   * @param <R> API DTO type
   * @param <P> response list type
   * @return the populated list response
   */
  @SuppressWarnings("unchecked")
  protected <D, R, P> P buildTypedListResponse(
      PageResponse<D> pageResponse, Function<D, R> mapper, Supplier<P> responseFactory) {
    var dp = buildListResponse(pageResponse, mapper);
    var response = responseFactory.get();
    try {
      var dataMethod = response.getClass().getMethod("setData", List.class);
      dataMethod.invoke(response, dp.data());
      var paginationMethod = response.getClass().getMethod("setPagination", Pagination.class);
      paginationMethod.invoke(response, dp.pagination());
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to populate list response — ensure the response type has setData(List) and"
              + " setPagination(Pagination)",
          e);
    }
    return (P) response;
  }
}
