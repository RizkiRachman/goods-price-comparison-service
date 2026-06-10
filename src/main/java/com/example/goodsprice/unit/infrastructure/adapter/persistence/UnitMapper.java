package com.example.goodsprice.unit.infrastructure.adapter.persistence;

import com.example.goodsprice.common.persistence.EntityMapperConfig;
import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import com.example.goodsprice.unit.infrastructure.adapter.persistence.entity.UnitEntity;
import org.mapstruct.Mapper;

@Mapper(config = EntityMapperConfig.class)
public interface UnitMapper {

  UnitEntity toEntity(UnitDomain domain);

  UnitDomain toDomain(UnitEntity entity);
}
