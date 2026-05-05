package com.example.goodsprice.shopping.application.domain.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StoreVisitDomain {

  private Long storeId;
  private String storeName;
  private String storeLocation;
  private List<ShoppingItemDomain> items;
  private Double subtotal;
  private String estimatedTime;
}
