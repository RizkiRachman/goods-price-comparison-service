package com.example.goodsprice.price.application.port.out;

import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import java.util.List;
import java.util.Set;

/**
 * Repository port for product price summary operations. Part of the driven adapter pattern in
 * hexagonal architecture.
 */
public interface PriceSummaryRepositoryPort {

  /**
   * Saves a single price summary. Creates new or updates existing.
   *
   * @param summary the price summary to save
   * @return the saved price summary
   */
  ProductPriceSummary save(ProductPriceSummary summary);

  /**
   * Saves multiple price summaries in batch.
   *
   * @param summaries the price summaries to save
   * @return the saved price summaries
   */
  List<ProductPriceSummary> saveAll(List<ProductPriceSummary> summaries);

  /**
   * Finds a price summary by product ID.
   *
   * @param productId the product ID
   * @return the price summary, or null if not found
   */
  ProductPriceSummary findByProductId(Long productId);

  /**
   * Finds price summaries for multiple products.
   *
   * @param productIds the set of product IDs
   * @return list of price summaries
   */
  List<ProductPriceSummary> findByProductIds(Set<Long> productIds);
}
