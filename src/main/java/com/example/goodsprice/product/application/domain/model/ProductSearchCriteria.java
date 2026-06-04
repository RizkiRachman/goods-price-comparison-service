package com.example.goodsprice.product.application.domain.model;

import com.example.goodsprice.common.constant.AppConstants;
import com.example.goodsprice.common.constant.SortConstants;
import java.util.List;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
  private List<Long> productIds;

  public String getSortBy() {
    return Objects.nonNull(sortBy) ? sortBy : SortConstants.NAME;
  }

  public String getSortDirection() {
    return Objects.nonNull(sortDirection) ? sortDirection : SortConstants.ASC;
  }

  public int getPage() {

    if (Objects.isNull(page) || page <= 0) {
      return 0;
    }
    return page - 1;
  }

  public int getSize() {
    if (Objects.isNull(size) || size <= 0) {
      return AppConstants.DEFAULT_PAGE_SIZE;
    }
    return Math.min(size, AppConstants.MAX_PAGE_SIZE);
  }

  public boolean hasSearch() {
    return Objects.nonNull(search) && !search.isBlank();
  }

  public boolean hasCategory() {
    return Objects.nonNull(category) && !category.isBlank();
  }

  public boolean hasBrand() {
    return Objects.nonNull(brand) && !brand.isBlank();
  }

  public boolean hasStatus() {
    return Objects.nonNull(status) && !status.isBlank();
  }

  public boolean shouldIncludePrice() {
    return Objects.nonNull(includePrice) && includePrice;
  }

  public boolean hasStoreId() {
    return Objects.nonNull(storeId) && !storeId.isBlank();
  }

  public boolean hasProductIds() {
    return Objects.nonNull(productIds) && !productIds.isEmpty();
  }

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

  public Long getStoreIdAsLong() {
    if (!isStoreIdNumeric()) {
      return null;
    }
    return Long.parseLong(storeId);
  }
}
