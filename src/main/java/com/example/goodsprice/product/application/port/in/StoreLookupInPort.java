package com.example.goodsprice.product.application.port.in;

import java.util.List;

/**
 * Port for looking up store information from the store service. This is a driving port consumed by
 * the product domain service to resolve store names/IDs.
 */
public interface StoreLookupInPort {

  /**
   * Finds store IDs by store name or chain (partial match supported).
   *
   * @param name the store name or chain to search for
   * @return list of matching store IDs
   */
  List<Long> findStoreIdsByName(String name);
}
