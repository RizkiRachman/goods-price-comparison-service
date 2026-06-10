package com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity;

import com.example.goodsprice.common.persistence.BaseTimestampEntity;
import com.example.goodsprice.receipt.application.domain.model.ReceiptStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "receipts")
public class ReceiptEntity extends BaseTimestampEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private UUID id;

  @Column(name = "image_hash", nullable = false, unique = true)
  private String imageHash;

  @Column(name = "original_filename")
  private String originalFilename;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ReceiptStatus status;

  @Column(name = "error_message")
  private String errorMessage;

  @Column(name = "store_name")
  private String storeName;

  @Column(name = "store_location")
  private String storeLocation;

  @Column(name = "receipt_date")
  private String receiptDate;

  @Column(name = "total_amount")
  private Double totalAmount;

  @Column(name = "extracted_data", columnDefinition = "TEXT")
  private String extractedDataJson;

  @Column(name = "image_data", columnDefinition = "BYTEA")
  private byte[] imageData;

  @Column(name = "processed_at")
  private LocalDateTime processedAt;

  public ReceiptEntity(String imageHash, String originalFilename) {
    this.imageHash = imageHash;
    this.originalFilename = originalFilename;
    this.status = ReceiptStatus.PENDING;
  }
}
