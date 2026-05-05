package com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "receipt_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "receipt_id", nullable = false)
  private UUID receiptId;

  @Column(name = "product_name")
  private String productName;

  @Column(name = "category")
  private String category;

  @Column(name = "quantity")
  private Double quantity;

  @Column(name = "unit_price")
  private Double unitPrice;

  @Column(name = "total_price")
  private Double totalPrice;

  @Column(name = "unit")
  private String unit;
}
