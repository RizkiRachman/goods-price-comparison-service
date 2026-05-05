package com.example.goodsprice.product.application.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class ProductDomain {

  private Long id;
  private String name;
  private String category;
  private String brand;
  private String unit;
  private String status;

  /** Timestamp of the most recent price update for this product. */
  private LocalDateTime lastPriceUpdate;

  /** Timestamp when the price summary was last calculated. */
  private LocalDateTime summaryLastCalculated;

  // Price summary fields - populated when includePrice=true

  /** Average price across all stores (current 90-day window). */
  private BigDecimal avgPrice;

  /** Minimum price across all stores (current 90-day window). */
  private BigDecimal minPrice;

  /** Maximum price across all stores (current 90-day window). */
  private BigDecimal maxPrice;

  /** Timestamp when the price data was last updated. */
  private LocalDateTime priceUpdatedAt;
}
