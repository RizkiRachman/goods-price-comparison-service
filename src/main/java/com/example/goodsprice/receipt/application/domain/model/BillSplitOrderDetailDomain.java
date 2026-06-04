package com.example.goodsprice.receipt.application.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BillSplitOrderDetailDomain {

  private String name;
  private Long productId;
  private Double quantity;
}
