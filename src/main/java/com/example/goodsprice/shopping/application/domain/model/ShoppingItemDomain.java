package com.example.goodsprice.shopping.application.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShoppingItemDomain {

  private String productName;
  private Double price;
  private Double quantity;
  private String unit;
}
