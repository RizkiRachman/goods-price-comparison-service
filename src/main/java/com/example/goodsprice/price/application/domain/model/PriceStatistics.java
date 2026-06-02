package com.example.goodsprice.price.application.domain.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceStatistics {

  private BigDecimal avgPrice;
  private BigDecimal minPrice;
  private BigDecimal maxPrice;
  private int storeCount;
  private int priceCount;

  public static PriceStatistics empty() {
    return new PriceStatistics(null, null, null, 0, 0);
  }
}
