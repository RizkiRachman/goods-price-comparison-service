package com.example.goodsprice.product.application.port.in;

import java.util.List;

/**
 * Port for querying product-related price information from the price service. This is a driving
 * port consumed by the product domain service to filter products by store availability.
 */
public interface ProductPriceQueryInPort {

  /**
   * Finds product IDs that have prices recorded for specific stores.
   *
   * @param storeIds the list of store IDs
   * @return list of product IDs available at the stores
   */
  List<Long> findProductIdsByStoreIds(List<Long> storeIds);
}
