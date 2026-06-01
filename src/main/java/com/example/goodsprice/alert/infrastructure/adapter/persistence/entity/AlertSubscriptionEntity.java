package com.example.goodsprice.alert.infrastructure.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "alert_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertSubscriptionEntity {

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

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
