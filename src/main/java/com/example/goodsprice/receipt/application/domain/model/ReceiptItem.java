package com.example.goodsprice.receipt.application.domain.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReceiptItem {

  private Long id;
  private UUID receiptId;
  private String productName;
  private String category;
  private Double quantity;
  private Double unitPrice;
  private Double totalPrice;
  private String unit;
}
