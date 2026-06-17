package com.example.goodsprice.store.infrastructure.adapter.web;

import static com.example.goodsprice.common.util.JsonNullableUtils.resolveNullable;

import com.example.goodsprice.api.model.CreateStoreRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Store;
import com.example.goodsprice.api.model.StoreListResponse;
import com.example.goodsprice.api.model.UpdateStoreRequest;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.common.web.AbstractCrudWebAdapter;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.in.StoreInPort;
import com.example.goodsprice.store.application.port.in.dto.CreateStoreCriteria;
import com.example.goodsprice.store.application.port.in.dto.StoreCriteria;
import com.example.goodsprice.store.application.port.in.dto.UpdateStoreCriteria;
import com.example.goodsprice.store.infrastructure.adapter.web.mapper.StoreDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreWebAdapter extends AbstractCrudWebAdapter {

  private final StoreInPort storeInPort;
  private final StoreDtoMapper mapper;

  public Store create(CreateStoreRequest request) {
    var criteria =
        CreateStoreCriteria.builder()
            .name(request.getName())
            .location(request.getLocation())
            .chain(request.getChain())
            .address(request.getAddress())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .build();
    StoreDomain domain = storeInPort.create(criteria);
    return mapper.toApiStore(domain);
  }

  public Store getById(Long id) {
    StoreDomain domain = storeInPort.findById(id);
    return mapper.toApiStore(domain);
  }

  public StoreListResponse list(
      Integer page,
      Integer pageSize,
      String sortBy,
      String sortOrder,
      String search,
      EntityStatus status,
      String chain,
      String location) {
    var params = resolvePagination(page, pageSize, sortBy, sortOrder, "name", "asc");
    var pageRequest = buildPageRequest(params);
    var criteria =
        new StoreCriteria(
            pageRequest,
            ObjectUtils.getOrNull(search, s -> s),
            resolveStatus(status),
            ObjectUtils.getOrNull(chain, s -> s),
            ObjectUtils.getOrNull(location, s -> s));

    var pageResponse = storeInPort.findAll(criteria);

    return buildTypedListResponse(pageResponse, mapper::toApiStore, StoreListResponse::new);
  }

  public Store update(Long id, UpdateStoreRequest request) {
    var criteria =
        UpdateStoreCriteria.builder()
            .id(id)
            .name(request.getName())
            .location(request.getLocation())
            .chain(resolveNullable(request.getChain()))
            .address(resolveNullable(request.getAddress()))
            .latitude(resolveNullable(request.getLatitude()))
            .longitude(resolveNullable(request.getLongitude()))
            .status(ObjectUtils.getOrNull(request.getStatus(), EntityStatus::getValue))
            .build();
    StoreDomain domain = storeInPort.update(criteria);
    return mapper.toApiStore(domain);
  }

  public void delete(Long id) {
    storeInPort.deleteById(id);
  }
}
