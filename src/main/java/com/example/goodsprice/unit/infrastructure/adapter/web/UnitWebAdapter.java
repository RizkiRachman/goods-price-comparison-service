package com.example.goodsprice.unit.infrastructure.adapter.web;

import com.example.goodsprice.api.model.CreateUnitRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Unit;
import com.example.goodsprice.api.model.UnitListResponse;
import com.example.goodsprice.api.model.UpdateUnitRequest;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.unit.application.port.in.UnitInPort;
import com.example.goodsprice.unit.infrastructure.adapter.web.mapper.UnitDtoMapper;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnitWebAdapter {

  private final UnitInPort unitInPort;
  private final UnitDtoMapper mapper;

  public Unit create(CreateUnitRequest request) {
    var domain =
        unitInPort.create(
            request.getId(),
            request.getName(),
            request.getSymbol(),
            ObjectUtils.getOrNull(request.getType(), CreateUnitRequest.TypeEnum::getValue),
            request.getDescription());
    return mapper.toApiUnit(domain);
  }

  public Unit findById(String id) {
    return mapper.toApiUnit(unitInPort.findById(id));
  }

  public UnitListResponse list(
      Integer page,
      Integer pageSize,
      String search,
      String type,
      EntityStatus status,
      String sortBy,
      String sortOrder) {
    var pageResponse =
        unitInPort.findAll(
            ObjectUtils.getOrDefault(page, p -> p, 1),
            ObjectUtils.getOrDefault(pageSize, s -> s, 20),
            ObjectUtils.getOrDefault(sortBy, s -> s, "name"),
            ObjectUtils.getOrDefault(sortOrder, s -> s, "asc"),
            search,
            type,
            ObjectUtils.getOrNull(status, EntityStatus::getValue));

    var response = new UnitListResponse();
    response.setData(pageResponse.content().stream().map(mapper::toApiUnit).toList());
    response.setPagination(pageResponse.toPagination());
    return response;
  }

  public Unit update(String id, UpdateUnitRequest request) {
    var domain =
        unitInPort.update(
            id,
            request.getName(),
            resolveNullable(request.getSymbol()),
            ObjectUtils.getOrNull(request.getType(), t -> t.name()),
            resolveNullable(request.getDescription()),
            ObjectUtils.getOrNull(request.getStatus(), EntityStatus::getValue));
    return mapper.toApiUnit(domain);
  }

  private <T> T resolveNullable(JsonNullable<T> nullable) {
    if (Objects.isNull(nullable)) return null;
    return nullable.orElse(null);
  }
}
