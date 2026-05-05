package com.example.goodsprice.price.application.port.in;

import com.example.goodsprice.price.application.domain.model.PriceDomain;
import java.time.LocalDate;
import java.util.List;

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

  PriceDomain findCheapestByProduct(Long productId);

  void deleteById(Long id);

  PriceDomain update(
      Long id, Double price, Double unitPrice, LocalDate dateRecorded, Boolean isPromo);
}
