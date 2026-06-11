package com.example.goodsprice.unit.infrastructure.adapter.web;

import static com.example.goodsprice.common.util.JsonNullableUtils.resolveNullable;

import com.example.goodsprice.api.model.CreateUnitRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Unit;
import com.example.goodsprice.api.model.UnitListResponse;
import com.example.goodsprice.api.model.UpdateUnitRequest;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.common.web.AbstractCrudWebAdapter;
import com.example.goodsprice.unit.application.port.in.UnitInPort;
import com.example.goodsprice.unit.application.port.in.dto.UnitCriteria;
import com.example.goodsprice.unit.infrastructure.adapter.web.mapper.UnitDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnitWebAdapter extends AbstractCrudWebAdapter {

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
    var params = resolvePagination(page, pageSize, sortBy, sortOrder, "name", "asc");
    var pageRequest = buildPageRequest(params);
    var criteria =
        new UnitCriteria(
            pageRequest, search, type, ObjectUtils.getOrNull(status, EntityStatus::getValue));

    var pageResponse = unitInPort.findAll(criteria);
    var dp = buildListResponse(pageResponse, mapper::toApiUnit);
    var response = new UnitListResponse();
    response.setData(dp.data());
    response.setPagination(dp.pagination());
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
}
