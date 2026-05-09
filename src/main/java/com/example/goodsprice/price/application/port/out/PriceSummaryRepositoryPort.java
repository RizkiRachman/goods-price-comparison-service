package com.example.goodsprice.price.application.port.out;

import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import java.util.List;
import java.util.Set;

public interface PriceSummaryRepositoryPort {

  ProductPriceSummary save(ProductPriceSummary summary);

  List<ProductPriceSummary> saveAll(List<ProductPriceSummary> summaries);

  ProductPriceSummary findByProductId(Long productId);

  List<ProductPriceSummary> findByProductIds(Set<Long> productIds);
}
