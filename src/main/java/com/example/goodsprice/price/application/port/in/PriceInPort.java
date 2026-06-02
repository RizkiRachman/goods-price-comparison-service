package com.example.goodsprice.price.application.port.in;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.price.application.domain.model.PriceCreateItem;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.in.dto.PriceCriteria;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PriceInPort {

  PriceDomain create(
      Long productId,
      Long storeId,
      Double price,
      Double unitPrice,
      LocalDate dateRecorded,
      Boolean isPromo);

  PriceDomain findById(Long id);

  List<PriceDomain> searchByProduct(Long productId, LocalDate startDate, LocalDate endDate);

  PageResponse<PriceDomain> searchByProduct(PriceCriteria criteria);

  PriceDomain findCheapestByProduct(Long productId);

  Map<Long, PriceDomain> findCheapestByProducts(List<Long> productIds);

  List<PriceDomain> findAllByProductIds(List<Long> productIds);

  void deleteById(Long id);

  PriceDomain update(
      Long id, Double price, Double unitPrice, LocalDate dateRecorded, Boolean isPromo);

  /**
   * Creates prices for multiple product-store combinations in batch.
   *
   * @param items list of price creation requests
   */
  void createBatch(List<PriceCreateItem> items);
}
