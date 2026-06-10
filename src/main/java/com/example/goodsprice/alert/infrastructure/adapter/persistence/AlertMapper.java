package com.example.goodsprice.alert.infrastructure.adapter.persistence;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;
import com.example.goodsprice.alert.infrastructure.adapter.persistence.entity.AlertSubscriptionEntity;
import com.example.goodsprice.common.persistence.EntityMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = EntityMapperConfig.class)
public interface AlertMapper {

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  AlertSubscriptionEntity toEntity(AlertSubscription domain);

  AlertSubscription toDomain(AlertSubscriptionEntity entity);
}
