package com.example.goodsprice.activity.infrastructure.adapter.persistence;

import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.infrastructure.adapter.persistence.entity.ActivityLogEntity;
import com.example.goodsprice.common.persistence.EntityMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = EntityMapperConfig.class)
public interface ActivityLogMapper {

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  ActivityLogEntity toEntity(ActivityLogDomain domain);

  ActivityLogDomain toDomain(ActivityLogEntity entity);
}
