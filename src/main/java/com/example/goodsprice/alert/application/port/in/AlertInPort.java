package com.example.goodsprice.alert.application.port.in;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;

@FunctionalInterface
public interface AlertInPort {

  AlertSubscription subscribe(AlertSubscription domain);
}
