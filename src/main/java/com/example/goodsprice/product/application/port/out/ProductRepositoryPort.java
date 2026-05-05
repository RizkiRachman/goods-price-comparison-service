package com.example.goodsprice.product.application.port.out;

import com.example.goodsprice.product.application.domain.model.ProductDomain;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepositoryPort {

  ProductDomain save(ProductDomain product);

  ProductDomain findById(Long id);

  ProductDomain findByName(String name);

  boolean existsByName(String name);

  List<ProductDomain> findAll();

  void deleteById(Long id);

  /**
   * Finds products that need their price summary recalculated. Returns products where
   * lastPriceUpdate > summaryLastCalculated or summaryLastCalculated is null.
   *
   * @param limit maximum number of products to return
   * @return list of products needing summary update
   */
  List<ProductDomain> findProductsNeedingSummaryUpdate(int limit);

  /**
   * Updates the summaryLastCalculated timestamp for a product.
   *
   * @param productId the product ID
   * @param timestamp the timestamp to set
   */
  void updateSummaryLastCalculated(Long productId, LocalDateTime timestamp);

  /**
   * Updates the lastPriceUpdate timestamp for a product.
   *
   * @param productId the product ID
   * @param timestamp the timestamp to set
   */
  void updateLastPriceUpdate(Long productId, LocalDateTime timestamp);
}
