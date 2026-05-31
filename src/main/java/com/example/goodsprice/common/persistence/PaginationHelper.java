package com.example.goodsprice.common.persistence;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.util.PaginationUtils;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Utility for paginated specification-based queries. Eliminates the sort/pageable/map/PageResponse
 * boilerplate from repository adapters.
 */
public final class PaginationHelper {

  private PaginationHelper() {}

  /**
   * Executes a paginated, specification-filtered query and maps entities to domain objects.
   *
   * @param pageRequest the page request with 1-based page, size, sort field and direction
   * @param spec the JPA specification for filtering
   * @param executor the JPA repository acting as specification executor
   * @param toDomain function to map entity to domain object
   * @param <T> domain type
   * @param <E> entity type
   * @return paginated domain response
   */
  public static <T, E> PageResponse<T> findAll(
      PageRequestDto pageRequest,
      Specification<E> spec,
      JpaSpecificationExecutor<E> executor,
      Function<E, T> toDomain) {
    var sort = resolveSort(pageRequest.sortBy(), pageRequest.sortDirection());
    var pageable =
        org.springframework.data.domain.PageRequest.of(
            pageRequest.toZeroBased(), pageRequest.size(), sort);
    Page<E> page = executor.findAll(spec, pageable);
    var domains = page.getContent().stream().map(toDomain).toList();
    return PageResponse.of(
        domains, pageRequest.page(), pageRequest.size(), page.getTotalElements());
  }

  private static Sort resolveSort(String sortBy, String sortDirection) {
    return Sort.by(
        "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC,
        PaginationUtils.resolveSortBy(sortBy, "id"));
  }
}
