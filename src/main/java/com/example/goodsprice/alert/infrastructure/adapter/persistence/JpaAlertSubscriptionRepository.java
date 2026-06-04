package com.example.goodsprice.alert.infrastructure.adapter.persistence;

import com.example.goodsprice.alert.infrastructure.adapter.persistence.entity.AlertSubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAlertSubscriptionRepository
    extends JpaRepository<AlertSubscriptionEntity, String> {}
