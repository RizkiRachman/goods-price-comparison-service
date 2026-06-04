package com.example.goodsprice.store.application.port.in;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.in.dto.CreateStoreCriteria;
import com.example.goodsprice.store.application.port.in.dto.StoreCriteria;
import com.example.goodsprice.store.application.port.in.dto.UpdateStoreCriteria;
import java.util.List;

public interface StoreInPort {

  StoreDomain create(CreateStoreCriteria criteria);

  StoreDomain findById(Long id);

  PageResponse<StoreDomain> findAll(StoreCriteria criteria);

  StoreDomain update(UpdateStoreCriteria criteria);

  void deleteById(Long id);

  List<StoreDomain> findAllById(List<Long> ids);
}
