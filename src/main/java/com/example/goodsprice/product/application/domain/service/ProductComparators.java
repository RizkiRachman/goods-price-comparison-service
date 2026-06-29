package com.example.goodsprice.product.application.domain.service;

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
            Comparator.comparing(ProductDomain::getName, Comparator.nullsLast(String::compareTo)),
            "category",
            Comparator.comparing(
                ProductDomain::getCategory, Comparator.nullsLast(String::compareTo)),
            "brand",
            Comparator.comparing(ProductDomain::getBrand, Comparator.nullsLast(String::compareTo)),
            "unit",
            Comparator.comparing(ProductDomain::getUnit, Comparator.nullsLast(String::compareTo)),
            "id",
            Comparator.comparing(ProductDomain::getId),
            "status",
            Comparator.comparing(
                ProductDomain::getStatus, Comparator.nullsLast(String::compareTo)));
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
