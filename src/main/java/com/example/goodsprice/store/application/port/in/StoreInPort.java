package com.example.goodsprice.store.application.port.in;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.in.dto.StoreCriteria;
import java.util.List;

public interface StoreInPort {

  StoreDomain create(
      String name,
      String location,
      String chain,
      String address,
      Double latitude,
      Double longitude);

  StoreDomain findById(Long id);

  PageResponse<StoreDomain> findAll(StoreCriteria criteria);

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

  List<StoreDomain> findAllById(List<Long> ids);
}
