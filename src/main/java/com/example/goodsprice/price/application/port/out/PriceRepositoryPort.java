package com.example.goodsprice.price.application.port.out;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.in.dto.PriceCriteria;
import java.time.LocalDate;
import java.util.List;

public interface PriceRepositoryPort extends GenericRepositoryPort<PriceDomain, Long> {

  List<PriceDomain> saveAll(Iterable<PriceDomain> prices);

  List<PriceDomain> findAll();

  List<PriceDomain> findByProductId(Long productId);

  List<PriceDomain> findByProductIdAndDateRange(
      Long productId, LocalDate startDate, LocalDate endDate);

  List<PriceDomain> findCheapestByProductId(Long productId);

  List<PriceDomain> findCheapestByProductIds(List<Long> productIds);

  List<PriceDomain> findAllByProductIds(List<Long> productIds);

  List<Long> findProductIdsByStoreIds(List<Long> storeIds);

  PageResponse<PriceDomain> findByProductIdWithFilters(PriceCriteria criteria);
}
