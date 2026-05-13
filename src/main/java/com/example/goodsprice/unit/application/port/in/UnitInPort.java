package com.example.goodsprice.unit.application.port.in;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.unit.application.domain.model.UnitDomain;

public interface UnitInPort {

  UnitDomain create(String id, String name, String symbol, String type, String description);

  UnitDomain findById(String id);

  PageResponse<UnitDomain> findAll(
      int page,
      int size,
      String sortBy,
      String sortDirection,
      String search,
      String type,
      String status);

  UnitDomain update(
      String id, String name, String symbol, String type, String description, String status);
}
