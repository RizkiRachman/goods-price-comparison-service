package com.example.goodsprice.alert.application.port.out;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;

public interface AlertRepositoryPort {
  AlertSubscription save(AlertSubscription subscription);

  AlertSubscription findById(String id);
}
