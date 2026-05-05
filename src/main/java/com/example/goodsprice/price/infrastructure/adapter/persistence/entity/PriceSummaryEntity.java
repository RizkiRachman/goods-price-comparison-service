package com.example.goodsprice.price.infrastructure.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_price_summaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceSummaryEntity {

  @Id
  @Column(name = "product_id")
  private Long productId;

  @Column(name = "avg_price", precision = 10, scale = 2)
  private BigDecimal avgPrice;

  @Column(name = "min_price", precision = 10, scale = 2)
  private BigDecimal minPrice;

  @Column(name = "max_price", precision = 10, scale = 2)
  private BigDecimal maxPrice;

  @Column(name = "store_count")
  private Integer storeCount;

  @Column(name = "price_count")
  private Integer priceCount;

  @Column(name = "last_calculated_at", nullable = false)
  private LocalDateTime lastCalculatedAt;

  @Column(name = "last_price_date")
  private LocalDate lastPriceDate;
}
