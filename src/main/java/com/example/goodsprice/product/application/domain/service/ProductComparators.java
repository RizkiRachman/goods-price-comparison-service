package com.example.goodsprice.product.application.domain.service;

import com.example.goodsprice.common.util.SortingUtils;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Component;

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

  public Comparator<ProductDomain> resolve(String sortBy) {
    if (Objects.isNull(sortBy)) {
      return null;
    }
    return comparators.get(sortBy.toLowerCase(Locale.ROOT));
  }

  public boolean hasComparator(String sortBy) {
    return Objects.nonNull(sortBy) && comparators.containsKey(sortBy.toLowerCase(Locale.ROOT));
  }
}
