package com.example.goodsprice.product.application.domain.service;

import com.example.goodsprice.common.util.SortingUtils;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Registry for ProductDomain comparators. Provides type-safe, null-safe comparator resolution for
 * product sorting.
 */
@Component
public class ProductComparators {

  private final Map<String, Comparator<ProductDomain>> comparators;

  public ProductComparators() {
    this.comparators =
        Map.of(
            "name",
            SortingUtils.comparingString(ProductDomain::getName),
            "category",
            SortingUtils.comparingString(ProductDomain::getCategory),
            "brand",
            SortingUtils.comparingString(ProductDomain::getBrand),
            "unit",
            SortingUtils.comparingString(ProductDomain::getUnit),
            "id",
            Comparator.comparing(ProductDomain::getId),
            "status",
            SortingUtils.comparingString(ProductDomain::getStatus));
  }

  /**
   * Resolves a comparator for the given sort field.
   *
   * @param sortBy the field name to sort by
   * @return the comparator, or null if not found
   */
  public Comparator<ProductDomain> resolve(String sortBy) {
    if (Objects.isNull(sortBy)) {
      return null;
    }
    return comparators.get(sortBy.toLowerCase(Locale.ROOT));
  }

  /**
   * Checks if a comparator exists for the given field.
   *
   * @param sortBy the field name to check
   * @return true if a comparator exists
   */
  public boolean hasComparator(String sortBy) {
    return Objects.nonNull(sortBy) && comparators.containsKey(sortBy.toLowerCase(Locale.ROOT));
  }
}
