package com.example.goodsprice.alert.infrastructure.adapter.web;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;
import com.example.goodsprice.alert.application.port.in.AlertInPort;
import com.example.goodsprice.alert.infrastructure.adapter.web.mapper.AlertDtoMapper;
import com.example.goodsprice.api.model.AlertSubscriptionRequest;
import com.example.goodsprice.api.model.AlertSubscriptionResponse;
import com.example.goodsprice.common.util.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertWebAdapter {

  private final AlertInPort alertInPort;
  private final AlertDtoMapper mapper;

  public AlertSubscriptionResponse subscribe(AlertSubscriptionRequest request) {
    var notificationMethod =
        ObjectUtils.getOrNull(request.getNotificationMethod(), Object::toString);
    var domain =
        AlertSubscription.builder()
            .productId(request.getProductId())
            .targetPrice(request.getTargetPrice())
            .notificationMethod(notificationMethod)
            .email(request.getEmail())
            .build();
    var subscription = alertInPort.subscribe(domain);

    var message = buildSubscriptionMessage(subscription, request);
    return mapper.toResponse(subscription, message);
  }

  private String buildSubscriptionMessage(
      AlertSubscription subscription, AlertSubscriptionRequest request) {
    var productName = subscription.getProductName();
    var targetPrice = subscription.getTargetPrice();
    var notificationMethod =
        ObjectUtils.getOrNull(request.getNotificationMethod(), Object::toString);

    return "Alert created for %s at %.2f via %s"
        .formatted(productName, targetPrice, notificationMethod);
  }
}
