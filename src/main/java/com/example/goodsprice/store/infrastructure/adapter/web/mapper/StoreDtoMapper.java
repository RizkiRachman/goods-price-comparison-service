package com.example.goodsprice.store.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.Store;
import com.example.goodsprice.common.web.mapper.DtoMapperSupport;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoreDtoMapper extends DtoMapperSupport {

  default Store toApiStore(StoreDomain domain) {
    return mapIfNotNull(
        domain,
        d -> {
          var result = new Store();
          result.setId(d.getId());
          result.setName(d.getName());
          result.setLocation(d.getLocation());
          result.setChain(d.getChain());
          result.setAddress(d.getAddress());
          result.setLatitude(d.getLatitude());
          result.setLongitude(d.getLongitude());
          result.setStatus(resolveStatusValue(d.getStatus()));
          return result;
        });
  }
}
