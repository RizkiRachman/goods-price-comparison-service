package com.example.goodsprice.store.application.port.out;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.in.dto.StoreCriteria;
import java.util.List;

public interface StoreRepositoryPort {

  StoreDomain save(StoreDomain store);

  StoreDomain findById(Long id);

  List<StoreDomain> findByName(String name);

  StoreDomain findByNameAndLocation(String name, String location);

  boolean existsByNameAndLocation(String name, String location);

  List<StoreDomain> findAllById(List<Long> ids);

  PageResponse<StoreDomain> findAll(StoreCriteria criteria);

  void deleteById(Long id);
}
