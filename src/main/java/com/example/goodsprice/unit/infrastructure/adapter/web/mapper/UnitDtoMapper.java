package com.example.goodsprice.unit.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.Unit;
import com.example.goodsprice.api.model.Unit.TypeEnum;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class UnitDtoMapper {

  public Unit toApiUnit(UnitDomain domain) {
    if (Objects.isNull(domain)) return null;
    var result = new Unit();
    result.setId(domain.getId());
    result.setName(domain.getName());
    result.setSymbol(domain.getSymbol());
    result.setType(ObjectUtils.getOrNull(domain.getType(), t -> TypeEnum.fromValue(t.name())));
    result.setDescription(
        ObjectUtils.getOrNull(
            domain.getDescription(), org.openapitools.jackson.nullable.JsonNullable::of));
    result.setStatus(
        ObjectUtils.getOrNull(
            domain.getStatus(), com.example.goodsprice.api.model.EntityStatus::fromValue));
    return result;
  }
}
