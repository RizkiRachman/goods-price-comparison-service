package com.example.goodsprice.alert.infrastructure.adapter.persistence.entity;

import com.example.goodsprice.common.persistence.BaseTimestampEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "alert_subscriptions")
public class AlertSubscriptionEntity extends BaseTimestampEntity {

  @Id
  @Column(name = "id", length = 36)
  private String id;

  @Column(name = "product_id", nullable = false)
  private Long productId;

  @Column(name = "product_name")
  private String productName;

  @Column(name = "target_price", nullable = false)
  private Double targetPrice;

  @Column(name = "current_price")
  private Double currentPrice;

  @Column(name = "notification_method", length = 50)
  private String notificationMethod;

  @Column(name = "email")
  private String email;

  @Column(name = "status", length = 20, nullable = false)
  private String status;
}
