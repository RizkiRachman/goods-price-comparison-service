package com.example.goodsprice.unit.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.Unit;
import com.example.goodsprice.api.model.Unit.TypeEnum;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.common.web.mapper.DtoMapperSupport;
import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UnitDtoMapper extends DtoMapperSupport {

  default Unit toApiUnit(UnitDomain domain) {
    return mapIfNotNull(
        domain,
        d -> {
          var result = new Unit();
          result.setId(d.getId());
          result.setName(d.getName());
          result.setSymbol(d.getSymbol());
          result.setType(ObjectUtils.getOrNull(d.getType(), t -> TypeEnum.fromValue(t.name())));
          result.setDescription(
              ObjectUtils.getOrNull(
                  d.getDescription(), org.openapitools.jackson.nullable.JsonNullable::of));
          result.setStatus(resolveStatusValue(d.getStatus()));
          return result;
        });
  }
}
