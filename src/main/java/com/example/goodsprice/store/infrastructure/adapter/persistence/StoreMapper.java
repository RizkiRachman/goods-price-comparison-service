package com.example.goodsprice.store.infrastructure.adapter.persistence;

import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.infrastructure.adapter.persistence.entity.StoreEntity;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class StoreMapper {

  public StoreEntity toEntity(StoreDomain domain) {
    if (Objects.isNull(domain)) return null;
    var entity = new StoreEntity();
    entity.setId(domain.getId());
    entity.setName(domain.getName());
    entity.setLocation(domain.getLocation());
    entity.setChain(domain.getChain());
    entity.setAddress(domain.getAddress());
    entity.setLatitude(domain.getLatitude());
    entity.setLongitude(domain.getLongitude());
    entity.setStatus(domain.getStatus());
    return entity;
  }

  public StoreDomain toDomain(StoreEntity entity) {
    if (Objects.isNull(entity)) return null;
    return StoreDomain.builder()
        .id(entity.getId())
        .name(entity.getName())
        .location(entity.getLocation())
        .chain(entity.getChain())
        .address(entity.getAddress())
        .latitude(entity.getLatitude())
        .longitude(entity.getLongitude())
        .status(entity.getStatus())
        .build();
  }
}
