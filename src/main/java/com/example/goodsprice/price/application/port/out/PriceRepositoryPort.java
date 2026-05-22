package com.example.goodsprice.price.application.port.out;

import com.example.goodsprice.common.dto.PageRequest;
import com.example.goodsprice.common.dto.PageResponse;
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

  List<PriceDomain> findAllByProductIds(List<Long> productIds);

  void deleteById(Long id);

  List<Long> findProductIdsByStoreIds(List<Long> storeIds);

  PageResponse<PriceDomain> findByProductIdWithFilters(
      Long productId,
      LocalDate startDate,
      LocalDate endDate,
      Long storeId,
      Boolean isPromo,
      PageRequest pageRequest);
}
