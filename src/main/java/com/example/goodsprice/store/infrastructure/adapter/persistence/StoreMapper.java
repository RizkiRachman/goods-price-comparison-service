package com.example.goodsprice.store.infrastructure.adapter.persistence;

import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.infrastructure.adapter.persistence.entity.StoreEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface StoreMapper {

  StoreEntity toEntity(StoreDomain domain);

  StoreDomain toDomain(StoreEntity entity);
}
