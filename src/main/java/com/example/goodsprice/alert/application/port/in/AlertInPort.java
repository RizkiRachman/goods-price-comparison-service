package com.example.goodsprice.alert.application.port.in;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface AlertInPort {

  AlertSubscription subscribe(
      Long productId, Double targetPrice, String notificationMethod, String email);
}
