package com.example.goodsprice.alert.infrastructure.adapter.persistence;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;
import com.example.goodsprice.alert.application.port.out.AlertRepositoryPort;
import com.example.goodsprice.alert.infrastructure.adapter.persistence.entity.AlertSubscriptionEntity;
import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import org.springframework.stereotype.Component;

@Component
public class AlertRepositoryAdapter
    extends AbstractRepositoryAdapter<AlertSubscription, String, AlertSubscriptionEntity>
    implements AlertRepositoryPort {

  private final JpaAlertSubscriptionRepository jpaRepository;
  private final AlertMapper mapper;

  public AlertRepositoryAdapter(JpaAlertSubscriptionRepository jpaRepository, AlertMapper mapper) {
    super(jpaRepository, mapper::toEntity, mapper::toDomain);
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }
}
