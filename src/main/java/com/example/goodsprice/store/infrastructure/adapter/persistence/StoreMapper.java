package com.example.goodsprice.store.infrastructure.adapter.persistence;

import com.example.goodsprice.common.persistence.EntityMapperConfig;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.infrastructure.adapter.persistence.entity.StoreEntity;
import org.mapstruct.Mapper;

@Mapper(config = EntityMapperConfig.class)
public interface StoreMapper {

  StoreEntity toEntity(StoreDomain domain);

  StoreDomain toDomain(StoreEntity entity);
}
