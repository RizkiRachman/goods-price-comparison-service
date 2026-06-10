package com.example.goodsprice.shopping.application.domain.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShoppingOptimizationResult {

  private Integer totalItems;
  private Double totalCost;
  private Integer storesToVisit;
  private List<StoreVisitDomain> route;
  private ShoppingSavingsDomain savings;

  public static final ShoppingOptimizationResult EMPTY =
      ShoppingOptimizationResult.builder()
          .totalItems(0)
          .totalCost(0.0)
          .storesToVisit(0)
          .route(List.of())
          .savings(ShoppingSavingsDomain.ZERO)
          .build();
}
