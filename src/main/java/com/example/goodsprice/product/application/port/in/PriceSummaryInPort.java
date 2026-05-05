package com.example.goodsprice.product.application.port.in;

import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import java.util.List;
import java.util.Set;

/**
 * Port for fetching product price summaries from the price service. This is a driving port consumed
 * by the product domain service to enrich product data with price information.
 */
public interface PriceSummaryInPort {

  /**
   * Finds price summaries for multiple products.
   *
   * @param productIds the set of product IDs
   * @return list of price summaries
   */
  List<ProductPriceSummary> findByProductIds(Set<Long> productIds);
}
