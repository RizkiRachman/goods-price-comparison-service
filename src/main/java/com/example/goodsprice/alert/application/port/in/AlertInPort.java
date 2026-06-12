package com.example.goodsprice.alert.application.port.in;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;

public interface AlertInPort {

  AlertSubscription subscribe(AlertSubscription domain);
}
