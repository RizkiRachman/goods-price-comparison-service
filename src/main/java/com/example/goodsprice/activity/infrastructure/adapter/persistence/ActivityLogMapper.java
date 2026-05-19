package com.example.goodsprice.activity.infrastructure.adapter.persistence;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import com.example.goodsprice.activity.infrastructure.adapter.persistence.entity.ActivityLogActionEntity;
import com.example.goodsprice.activity.infrastructure.adapter.persistence.entity.ActivityLogEntity;
import com.example.goodsprice.activity.infrastructure.adapter.persistence.entity.ActivityLogTypeEntity;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapper {

  public ActivityLogEntity toEntity(ActivityLogDomain domain) {
    if (Objects.isNull(domain)) return null;
    var now = LocalDateTime.now();
    return ActivityLogEntity.builder()
        .type(toEntityType(domain.getType()))
        .action(toEntityAction(domain.getAction()))
        .description(domain.getDescription())
        .createdAt(Objects.nonNull(domain.getCreatedAt()) ? domain.getCreatedAt() : now)
        .updatedAt(Objects.nonNull(domain.getUpdatedAt()) ? domain.getUpdatedAt() : now)
        .build();
  }

  public ActivityLogDomain toDomain(ActivityLogEntity entity) {
    if (Objects.isNull(entity)) return null;
    return ActivityLogDomain.builder()
        .id(entity.getId())
        .type(toDomainType(entity.getType()))
        .action(toDomainAction(entity.getAction()))
        .description(entity.getDescription())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();
  }

  public static ActivityLogTypeEntity toEntityType(ActivityLogType type) {
    if (Objects.isNull(type)) return null;
    return ActivityLogTypeEntity.valueOf(type.name());
  }

  public static ActivityLogType toDomainType(ActivityLogTypeEntity type) {
    if (Objects.isNull(type)) return null;
    return ActivityLogType.valueOf(type.name());
  }

  public static ActivityLogActionEntity toEntityAction(ActivityLogAction action) {
    if (Objects.isNull(action)) return null;
    return ActivityLogActionEntity.valueOf(action.name());
  }

  public static ActivityLogAction toDomainAction(ActivityLogActionEntity action) {
    if (Objects.isNull(action)) return null;
    return ActivityLogAction.valueOf(action.name());
  }
}
