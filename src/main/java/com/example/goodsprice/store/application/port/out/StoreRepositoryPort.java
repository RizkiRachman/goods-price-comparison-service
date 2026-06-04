package com.example.goodsprice.store.application.port.out;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.in.dto.StoreCriteria;
import java.util.List;

public interface StoreRepositoryPort extends GenericRepositoryPort<StoreDomain, Long> {

  List<StoreDomain> findByName(String name);

  StoreDomain findByNameAndLocation(String name, String location);

  boolean existsByNameAndLocation(String name, String location);

  List<StoreDomain> findAllById(List<Long> ids);

  PageResponse<StoreDomain> findAll(StoreCriteria criteria);
}
