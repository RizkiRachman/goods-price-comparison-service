package com.example.goodsprice.receipt.application.domain.model;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReceiptCorrectionDomain {

  private String storeName;
  private String storeLocation;
  private String receiptDate;
  private BigDecimal totalAmount;
  private List<ReceiptItemCorrectionDomain> items;
}
