package com.example.goodsprice.alert.infrastructure.adapter.persistence;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;
import com.example.goodsprice.alert.application.port.out.AlertRepositoryPort;
import com.example.goodsprice.alert.infrastructure.adapter.persistence.entity.AlertSubscriptionEntity;
import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class AlertRepositoryAdapter
    extends AbstractRepositoryAdapter<AlertSubscription, String, AlertSubscriptionEntity>
    implements AlertRepositoryPort {

  private final JpaAlertSubscriptionRepository jpaRepository;
  private final AlertMapper mapper;

  public AlertRepositoryAdapter(JpaAlertSubscriptionRepository jpaRepository, AlertMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  protected JpaRepository<AlertSubscriptionEntity, String> getJpaRepository() {
    return jpaRepository;
  }

  @Override
  protected AlertSubscriptionEntity toEntity(AlertSubscription domain) {
    return mapper.toEntity(domain);
  }

  @Override
  protected AlertSubscription toDomain(AlertSubscriptionEntity entity) {
    return mapper.toDomain(entity);
  }
}
