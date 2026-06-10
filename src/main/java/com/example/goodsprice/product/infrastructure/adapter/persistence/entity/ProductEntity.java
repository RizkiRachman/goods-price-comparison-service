package com.example.goodsprice.product.infrastructure.adapter.persistence.entity;

import com.example.goodsprice.common.persistence.BaseTimestampEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class ProductEntity extends BaseTimestampEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "category")
  private String category;

  @Column(name = "brand")
  private String brand;

  @Column(name = "unit")
  private String unit;

  @Column(name = "status")
  private String status;

  @Column(name = "last_price_update")
  private LocalDateTime lastPriceUpdate;

  @Column(name = "summary_last_calculated")
  private LocalDateTime summaryLastCalculated;
}
