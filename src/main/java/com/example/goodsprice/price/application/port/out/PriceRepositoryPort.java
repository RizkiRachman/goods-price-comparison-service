package com.example.goodsprice.price.application.port.out;

import com.example.goodsprice.price.application.domain.model.PriceDomain;
import java.time.LocalDate;
import java.util.List;

public interface PriceRepositoryPort {

  PriceDomain save(PriceDomain price);

  PriceDomain findById(Long id);

  List<PriceDomain> findAll();

  List<PriceDomain> findByProductId(Long productId);

  List<PriceDomain> findByProductIdAndDateRange(
      Long productId, LocalDate startDate, LocalDate endDate);

  List<PriceDomain> findCheapestByProductId(Long productId);

  List<PriceDomain> findCheapestByProductIds(List<Long> productIds);

  void deleteById(Long id);

  /**
   * Finds distinct product IDs that have prices recorded for specific stores.
   *
   * @param storeIds the list of store IDs
   * @return list of distinct product IDs
   */
  List<Long> findProductIdsByStoreIds(List<Long> storeIds);
}
