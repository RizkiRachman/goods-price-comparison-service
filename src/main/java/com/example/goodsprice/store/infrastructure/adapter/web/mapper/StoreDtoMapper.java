package com.example.goodsprice.store.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Store;
import com.example.goodsprice.common.web.mapper.DtoMapperSupport;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface StoreDtoMapper extends DtoMapperSupport {

  @Mapping(target = "status", qualifiedByName = "mapStatus")
  Store toApiStore(StoreDomain domain);

  @Named("mapStatus")
  default EntityStatus mapStatus(String status) {
    return resolveStatusValue(status);
  }
}
