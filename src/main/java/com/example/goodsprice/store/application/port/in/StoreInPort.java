package com.example.goodsprice.store.application.port.in;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.store.application.domain.model.StoreDomain;

public interface StoreInPort {

  StoreDomain create(
      String name,
      String location,
      String chain,
      String address,
      Double latitude,
      Double longitude);

  StoreDomain findById(Long id);

  PageResponse<StoreDomain> findAll(
      int page,
      int size,
      String sortBy,
      String sortDirection,
      String search,
      String status,
      String chain,
      String location);

  StoreDomain update(
      Long id,
      String name,
      String location,
      String chain,
      String address,
      Double latitude,
      Double longitude,
      String status);

  void deleteById(Long id);
}
