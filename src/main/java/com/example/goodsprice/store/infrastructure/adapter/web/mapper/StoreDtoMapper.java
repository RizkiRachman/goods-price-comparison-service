package com.example.goodsprice.store.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Store;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Objects;

@Mapper(
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface StoreDtoMapper {

  @Mapping(target = "status", qualifiedByName = "mapStatus")
  Store toApiStore(StoreDomain domain);

  @Named("mapStatus")
  default EntityStatus mapStatus(String status) {
    if (Objects.isNull(status)) return null;
    return EntityStatus.fromValue(status);
  }
}
