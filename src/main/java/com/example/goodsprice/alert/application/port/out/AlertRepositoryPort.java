package com.example.goodsprice.alert.application.port.out;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;
import com.example.goodsprice.common.repository.GenericRepositoryPort;

public interface AlertRepositoryPort extends GenericRepositoryPort<AlertSubscription, String> {}
