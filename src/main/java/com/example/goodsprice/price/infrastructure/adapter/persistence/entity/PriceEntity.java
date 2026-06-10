package com.example.goodsprice.price.infrastructure.adapter.persistence.entity;

import com.example.goodsprice.common.persistence.BaseTimestampEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "prices")
public class PriceEntity extends BaseTimestampEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "product_id", nullable = false)
  private Long productId;

  @Column(name = "store_id", nullable = false)
  private Long storeId;

  @Column(name = "price", nullable = false)
  private Double price;

  @Column(name = "unit_price")
  private Double unitPrice;

  @Column(name = "date_recorded", nullable = false)
  private LocalDate dateRecorded;

  @Column(name = "is_promo", nullable = false)
  private Boolean isPromo = false;
}
