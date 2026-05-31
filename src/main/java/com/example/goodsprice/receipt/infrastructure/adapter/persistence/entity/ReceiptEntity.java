package com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptEntity {

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
  private ReceiptStatusEntity status;

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

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Lob
  @Column(name = "image_data", columnDefinition = "BYTEA")
  private byte[] imageData;

  @Column(name = "processed_at")
  private LocalDateTime processedAt;

  public ReceiptEntity(String imageHash, String originalFilename) {
    this.imageHash = imageHash;
    this.originalFilename = originalFilename;
    this.status = ReceiptStatusEntity.PENDING;
  }
}
