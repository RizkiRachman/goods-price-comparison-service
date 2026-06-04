package com.example.goodsprice.product.application.domain.model;

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

  private LocalDateTime lastPriceUpdate;

  private LocalDateTime summaryLastCalculated;
}
