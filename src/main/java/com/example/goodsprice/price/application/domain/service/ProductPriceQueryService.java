package com.example.goodsprice.price.application.domain.service;

import com.example.goodsprice.price.application.port.out.PriceRepositoryPort;
import com.example.goodsprice.product.application.port.in.ProductPriceQueryInPort;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductPriceQueryService implements ProductPriceQueryInPort {

  private final PriceRepositoryPort priceRepository;

  @Override
  public List<Long> findProductIdsByStoreIds(List<Long> storeIds) {
    if (Objects.isNull(storeIds) || storeIds.isEmpty()) {
      return List.of();
    }
    return priceRepository.findProductIdsByStoreIds(storeIds);
  }
}
