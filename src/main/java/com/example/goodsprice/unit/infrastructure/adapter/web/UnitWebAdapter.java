package com.example.goodsprice.unit.infrastructure.adapter.web;

import static com.example.goodsprice.common.util.JsonNullableUtils.resolveNullable;

import com.example.goodsprice.api.model.CreateUnitRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Unit;
import com.example.goodsprice.api.model.UnitListResponse;
import com.example.goodsprice.api.model.UpdateUnitRequest;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.common.web.AbstractCrudWebAdapter;
import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import com.example.goodsprice.unit.application.domain.model.UnitType;
import com.example.goodsprice.unit.application.port.in.UnitInPort;
import com.example.goodsprice.unit.application.port.in.dto.UnitCriteria;
import com.example.goodsprice.unit.infrastructure.adapter.web.mapper.UnitDtoMapper;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnitWebAdapter extends AbstractCrudWebAdapter {

  private final UnitInPort unitInPort;
  private final UnitDtoMapper mapper;

  public Unit create(CreateUnitRequest request) {
    var typeStr = ObjectUtils.getOrNull(request.getType(), CreateUnitRequest.TypeEnum::getValue);
    var domain =
        UnitDomain.builder()
            .id(request.getId())
            .name(request.getName())
            .symbol(request.getSymbol())
            .type(
                Objects.nonNull(typeStr)
                    ? UnitType.valueOf(typeStr.toUpperCase(Locale.ROOT))
                    : null)
            .description(request.getDescription())
            .build();
    return mapper.toApiUnit(unitInPort.create(domain));
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
    return buildCompleteListResponse(
        pageResponse,
        mapper::toApiUnit,
        (data, pagination) -> {
          var r = new UnitListResponse();
          r.setData(data);
          r.setPagination(pagination);
          return r;
        });
  }

  public Unit update(String id, UpdateUnitRequest request) {
    var typeStr = ObjectUtils.getOrNull(request.getType(), t -> t.name());
    var domain =
        UnitDomain.builder()
            .name(request.getName())
            .symbol(resolveNullable(request.getSymbol()))
            .type(
                Objects.nonNull(typeStr)
                    ? UnitType.valueOf(typeStr.toUpperCase(Locale.ROOT))
                    : null)
            .description(resolveNullable(request.getDescription()))
            .status(ObjectUtils.getOrNull(request.getStatus(), EntityStatus::getValue))
            .build();
    return mapper.toApiUnit(unitInPort.update(id, domain));
  }
}
