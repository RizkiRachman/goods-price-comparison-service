package com.example.goodsprice.alert.infrastructure.adapter.persistence;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;
import com.example.goodsprice.alert.infrastructure.adapter.persistence.entity.AlertSubscriptionEntity;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class AlertMapper {

  public AlertSubscriptionEntity toEntity(AlertSubscription domain) {
    if (Objects.isNull(domain)) return null;
    var entity = new AlertSubscriptionEntity();
    entity.setId(domain.getId());
    entity.setProductId(domain.getProductId());
    entity.setProductName(domain.getProductName());
    entity.setTargetPrice(domain.getTargetPrice());
    entity.setCurrentPrice(domain.getCurrentPrice());
    entity.setNotificationMethod(domain.getNotificationMethod());
    entity.setEmail(domain.getEmail());
    entity.setStatus(domain.getStatus());
    return entity;
  }

  public AlertSubscription toDomain(AlertSubscriptionEntity entity) {
    if (Objects.isNull(entity)) return null;
    return AlertSubscription.builder()
        .id(entity.getId())
        .productId(entity.getProductId())
        .productName(entity.getProductName())
        .targetPrice(entity.getTargetPrice())
        .currentPrice(entity.getCurrentPrice())
        .notificationMethod(entity.getNotificationMethod())
        .email(entity.getEmail())
        .status(entity.getStatus())
        .build();
  }
}
