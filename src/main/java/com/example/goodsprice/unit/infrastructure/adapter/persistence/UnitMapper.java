package com.example.goodsprice.unit.infrastructure.adapter.persistence;

import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import com.example.goodsprice.unit.infrastructure.adapter.persistence.entity.UnitEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface UnitMapper {

  UnitEntity toEntity(UnitDomain domain);

  UnitDomain toDomain(UnitEntity entity);
}
