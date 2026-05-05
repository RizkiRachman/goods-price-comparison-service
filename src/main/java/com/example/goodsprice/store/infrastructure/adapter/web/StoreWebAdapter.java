package com.example.goodsprice.store.infrastructure.adapter.web;

import com.example.goodsprice.api.model.CreateStoreRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Store;
import com.example.goodsprice.api.model.StoreListResponse;
import com.example.goodsprice.api.model.UpdateStoreRequest;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.in.StoreInPort;
import com.example.goodsprice.store.infrastructure.adapter.web.mapper.StoreDtoMapper;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreWebAdapter {

  private final StoreInPort storeInPort;
  private final StoreDtoMapper mapper;

  public Store create(CreateStoreRequest request) {
    StoreDomain domain =
        storeInPort.create(
            request.getName(),
            request.getLocation(),
            request.getChain(),
            request.getAddress(),
            request.getLatitude(),
            request.getLongitude());
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
    var pageValue = ObjectUtils.getOrDefault(page, p -> p, 0);
    var sizeValue = ObjectUtils.getOrDefault(pageSize, s -> s, 20);
    var sortByValue = ObjectUtils.getOrNull(sortBy, s -> s);
    var sortDirValue = ObjectUtils.getOrNull(sortOrder, s -> s);
    var searchValue = ObjectUtils.getOrNull(search, s -> s);
    var statusValue = ObjectUtils.getOrNull(status, EntityStatus::getValue);
    var chainValue = ObjectUtils.getOrNull(chain, s -> s);
    var locationValue = ObjectUtils.getOrNull(location, s -> s);

    var pageResponse =
        storeInPort.findAll(
            pageValue,
            sizeValue,
            sortByValue,
            sortDirValue,
            searchValue,
            statusValue,
            chainValue,
            locationValue);

    var response = new StoreListResponse();
    response.setData(pageResponse.content().stream().map(mapper::toApiStore).toList());
    response.setPagination(pageResponse.toPagination());
    return response;
  }

  public Store update(Long id, UpdateStoreRequest request) {
    StoreDomain domain =
        storeInPort.update(
            id,
            request.getName(),
            request.getLocation(),
            resolveNullable(request.getChain()),
            resolveNullable(request.getAddress()),
            resolveNullable(request.getLatitude()),
            resolveNullable(request.getLongitude()),
            ObjectUtils.getOrNull(request.getStatus(), EntityStatus::getValue));
    return mapper.toApiStore(domain);
  }

  public void delete(Long id) {
    storeInPort.deleteById(id);
  }

  private <T> T resolveNullable(org.openapitools.jackson.nullable.JsonNullable<T> nullable) {
    if (Objects.isNull(nullable)) return null;
    return nullable.orElse(null);
  }
}
