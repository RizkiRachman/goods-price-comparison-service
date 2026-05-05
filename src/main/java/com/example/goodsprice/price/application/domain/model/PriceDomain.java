package com.example.goodsprice.price.application.domain.model;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PriceDomain {

  private Long id;
  private Long productId;
  private Long storeId;
  private Double price;
  private Double unitPrice;
  private LocalDate dateRecorded;
  private Boolean isPromo;
}
