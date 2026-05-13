package com.example.goodsprice.unit.infrastructure.adapter.persistence;

import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import com.example.goodsprice.unit.infrastructure.adapter.persistence.entity.UnitEntity;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class UnitMapper {

  public UnitEntity toEntity(UnitDomain domain) {
    if (Objects.isNull(domain)) return null;
    var entity = new UnitEntity();
    entity.setId(domain.getId());
    entity.setName(domain.getName());
    entity.setSymbol(domain.getSymbol());
    entity.setType(domain.getType());
    entity.setDescription(domain.getDescription());
    entity.setStatus(domain.getStatus());
    return entity;
  }

  public UnitDomain toDomain(UnitEntity entity) {
    if (Objects.isNull(entity)) return null;
    return UnitDomain.builder()
        .id(entity.getId())
        .name(entity.getName())
        .symbol(entity.getSymbol())
        .type(entity.getType())
        .description(entity.getDescription())
        .status(entity.getStatus())
        .build();
  }
}
