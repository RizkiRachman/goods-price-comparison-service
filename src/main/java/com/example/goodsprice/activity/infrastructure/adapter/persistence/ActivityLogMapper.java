package com.example.goodsprice.activity.infrastructure.adapter.persistence;

import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.infrastructure.adapter.persistence.entity.ActivityLogEntity;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapper {

  public ActivityLogEntity toEntity(ActivityLogDomain domain) {
    if (Objects.isNull(domain)) return null;
    var now = LocalDateTime.now();
    return ActivityLogEntity.builder()
        .id(Objects.nonNull(domain.getId()) ? domain.getId() : UUID.randomUUID())
        .type(domain.getType())
        .action(domain.getAction())
        .description(domain.getDescription())
        .createdAt(Objects.nonNull(domain.getCreatedAt()) ? domain.getCreatedAt() : now)
        .updatedAt(Objects.nonNull(domain.getUpdatedAt()) ? domain.getUpdatedAt() : now)
        .build();
  }

  public ActivityLogDomain toDomain(ActivityLogEntity entity) {
    if (Objects.isNull(entity)) return null;
    return ActivityLogDomain.builder()
        .id(entity.getId())
        .type(entity.getType())
        .action(entity.getAction())
        .description(entity.getDescription())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();
  }
}
