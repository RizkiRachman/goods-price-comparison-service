package com.example.goodsprice.alert.application.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertSubscription {

  private String id;
  private Long productId;
  private String productName;
  private Double targetPrice;
  private Double currentPrice;
  private String notificationMethod;
  private String email;
  private String status;
}
