package com.example.goodsprice.receipt.application.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BillSplitItemDomain {

  private Long productId;
  private String productName;
  private Double quantity;
  private Double unitPrice;
  private Double subtotal;
}
