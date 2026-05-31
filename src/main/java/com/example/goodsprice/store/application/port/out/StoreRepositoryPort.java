package com.example.goodsprice.store.application.port.out;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import java.util.List;

public interface StoreRepositoryPort {

  StoreDomain save(StoreDomain store);

  StoreDomain findById(Long id);

  List<StoreDomain> findByName(String name);

  StoreDomain findByNameAndLocation(String name, String location);

  boolean existsByNameAndLocation(String name, String location);

  List<StoreDomain> findAllById(List<Long> ids);

  PageResponse<StoreDomain> findAll(
      PageRequestDto pageRequest, String search, String status, String chain, String location);

  void deleteById(Long id);
}
