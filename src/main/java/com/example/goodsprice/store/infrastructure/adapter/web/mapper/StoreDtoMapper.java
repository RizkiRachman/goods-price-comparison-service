package com.example.goodsprice.store.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Store;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class StoreDtoMapper {

  public Store toApiStore(StoreDomain domain) {
    if (Objects.isNull(domain)) return null;
    var result = new Store();
    result.setId(domain.getId());
    result.setName(domain.getName());
    result.setLocation(domain.getLocation());
    result.setChain(domain.getChain());
    result.setAddress(domain.getAddress());
    result.setLatitude(domain.getLatitude());
    result.setLongitude(domain.getLongitude());
    result.setStatus(ObjectUtils.getOrNull(domain.getStatus(), EntityStatus::fromValue));
    return result;
  }
}
