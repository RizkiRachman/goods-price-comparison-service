package com.example.goodsprice.price.application.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductPriceSummary {

  private Long productId;

  private BigDecimal avgPrice;
  private BigDecimal minPrice;
  private BigDecimal maxPrice;
  private Integer storeCount;
  private Integer priceCount;

  private LocalDateTime lastCalculatedAt;
  private LocalDate lastPriceDate;
}
