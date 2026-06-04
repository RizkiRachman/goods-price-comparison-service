package com.example.goodsprice.receipt.application.domain.model;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReceiptDomain {

  private UUID id;
  private String imageHash;
  private String originalFilename;
  private ReceiptStatus status;
  private String storeName;
  private String storeLocation;
  private String receiptDate;
  private BigDecimal totalAmount;
  private String extractedDataJson;
  private String errorMessage;
  private byte[] imageData;

  public void markAsProcessing() {
    this.status = ReceiptStatus.PROCESSING;
  }

  public void markAsCompleted(
      String storeName,
      String storeLocation,
      String receiptDate,
      BigDecimal totalAmount,
      String extractedDataJson) {
    this.storeName = storeName;
    this.storeLocation = storeLocation;
    this.receiptDate = receiptDate;
    this.totalAmount = totalAmount;
    this.extractedDataJson = extractedDataJson;
    this.status = ReceiptStatus.COMPLETED;
  }

  public void markAsFailed(String errorMessage) {
    this.status = ReceiptStatus.FAILED;
    this.errorMessage = errorMessage;
  }

  public void markAsApproved() {
    this.status = ReceiptStatus.APPROVED;
  }

  public void markAsRejected() {
    this.status = ReceiptStatus.REJECTED;
  }

  public boolean isRetryable() {
    return status == ReceiptStatus.FAILED;
  }
}
