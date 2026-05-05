package com.example.goodsprice.product.application.domain.model;

import com.example.goodsprice.common.constant.AppConstants;
import com.example.goodsprice.common.constant.SortConstants;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Search criteria for product queries. Encapsulates all filtering, sorting, and pagination
 * parameters.
 */
@Getter
@Setter
@Builder
public class ProductSearchCriteria {

  private String search;
  private String category;
  private String brand;
  private String status;
  private String sortBy;
  private String sortDirection;
  private Integer page;
  private Integer size;
  private Boolean includePrice;
  private String storeId;

  /** Returns the sort field, defaulting to "name" if not specified. */
  public String getSortBy() {
    return Objects.nonNull(sortBy) ? sortBy : SortConstants.NAME;
  }

  /** Returns the sort direction, defaulting to "asc" if not specified. */
  public String getSortDirection() {
    return Objects.nonNull(sortDirection) ? sortDirection : SortConstants.ASC;
  }

  /**
   * Returns the page number for database queries (0-indexed). API uses 1-indexed pages, so we
   * convert by subtracting 1.
   *
   * @return 0-indexed page number for database queries
   */
  public int getPage() {
    // API uses 1-indexed pages, convert to 0-indexed for database
    if (Objects.isNull(page) || page <= 0) {
      return 0;
    }
    return page - 1;
  }

  /** Returns the page size, defaulting to AppConstants.DEFAULT_PAGE_SIZE if not specified. */
  public int getSize() {
    if (Objects.isNull(size) || size <= 0) {
      return AppConstants.DEFAULT_PAGE_SIZE;
    }
    return Math.min(size, AppConstants.MAX_PAGE_SIZE);
  }

  /** Checks if a search term is present. */
  public boolean hasSearch() {
    return Objects.nonNull(search) && !search.isBlank();
  }

  /** Checks if a category filter is present. */
  public boolean hasCategory() {
    return Objects.nonNull(category) && !category.isBlank();
  }

  /** Checks if a brand filter is present. */
  public boolean hasBrand() {
    return Objects.nonNull(brand) && !brand.isBlank();
  }

  /** Checks if a status filter is present. */
  public boolean hasStatus() {
    return Objects.nonNull(status) && !status.isBlank();
  }

  /** Checks if price details should be included. */
  public boolean shouldIncludePrice() {
    return Objects.nonNull(includePrice) && includePrice;
  }

  /** Checks if a store filter is present. */
  public boolean hasStoreId() {
    return Objects.nonNull(storeId) && !storeId.isBlank();
  }

  /**
   * Checks if the storeId represents a numeric store ID.
   *
   * @return true if storeId is numeric, false if it's a name/chain
   */
  public boolean isStoreIdNumeric() {
    if (Objects.isNull(storeId) || storeId.isBlank()) {
      return false;
    }
    try {
      Long.parseLong(storeId);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Returns the storeId as a Long if it's numeric.
   *
   * @return the numeric store ID, or null if not numeric
   */
  public Long getStoreIdAsLong() {
    if (!isStoreIdNumeric()) {
      return null;
    }
    return Long.parseLong(storeId);
  }
}
