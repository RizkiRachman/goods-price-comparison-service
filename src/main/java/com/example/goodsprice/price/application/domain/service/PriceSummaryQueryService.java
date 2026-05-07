package com.example.goodsprice.price.application.domain.service;

import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import com.example.goodsprice.price.application.port.out.PriceSummaryRepositoryPort;
import com.example.goodsprice.product.application.port.in.PriceSummaryInPort;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceSummaryQueryService implements PriceSummaryInPort {

  private final PriceSummaryRepositoryPort priceSummaryRepository;

  @Override
  public List<ProductPriceSummary> findByProductIds(Set<Long> productIds) {
    if (Objects.isNull(productIds) || productIds.isEmpty()) {
      return List.of();
    }
    return priceSummaryRepository.findByProductIds(productIds);
  }
}
