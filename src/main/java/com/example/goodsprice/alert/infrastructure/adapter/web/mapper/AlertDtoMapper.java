package com.example.goodsprice.alert.infrastructure.adapter.web.mapper;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;
import com.example.goodsprice.api.model.AlertSubscriptionResponse;
import com.example.goodsprice.api.model.AlertSubscriptionResponse.StatusEnum;
import com.example.goodsprice.common.util.ObjectUtils;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class AlertDtoMapper {

  public AlertSubscriptionResponse toResponse(AlertSubscription subscription, String message) {
    var response = new AlertSubscriptionResponse();
    response.setSubscriptionId(subscription.getId());
    response.setStatus(
        ObjectUtils.getOrDefault(
            subscription.getStatus(), AlertDtoMapper::mapStatus, StatusEnum.ACTIVE));
    response.setProductName(subscription.getProductName());
    response.setCurrentPrice(subscription.getCurrentPrice());
    response.setTargetPrice(subscription.getTargetPrice());
    response.setMessage(message);
    return response;
  }

  public static StatusEnum mapStatus(String status) {
    if (Objects.isNull(status)) return StatusEnum.ACTIVE;
    return switch (status) {
      case "PAUSED" -> StatusEnum.PAUSED;
      case "EXPIRED" -> StatusEnum.EXPIRED;
      default -> StatusEnum.ACTIVE;
    };
  }
}
