package com.example.goodsprice.product.application.port.in;

import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import java.util.List;
import java.util.Set;

@FunctionalInterface
public interface PriceSummaryInPort {

  List<ProductPriceSummary> findByProductIds(Set<Long> productIds);
}
