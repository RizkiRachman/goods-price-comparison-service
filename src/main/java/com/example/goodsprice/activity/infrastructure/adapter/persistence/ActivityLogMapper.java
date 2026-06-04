package com.example.goodsprice.activity.infrastructure.adapter.persistence;

import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.infrastructure.adapter.persistence.entity.ActivityLogEntity;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapper {

  public ActivityLogEntity toEntity(ActivityLogDomain domain) {
    if (Objects.isNull(domain)) return null;
    var entity = new ActivityLogEntity();
    entity.setType(domain.getType());
    entity.setAction(domain.getAction());
    entity.setDescription(domain.getDescription());
    // createdAt and updatedAt are auto-managed by Hibernate @CreationTimestamp/@UpdateTimestamp
    return entity;
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
