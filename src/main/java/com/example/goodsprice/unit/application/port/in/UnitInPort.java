package com.example.goodsprice.unit.application.port.in;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import com.example.goodsprice.unit.application.port.in.dto.UnitCriteria;

public interface UnitInPort {

  UnitDomain create(UnitDomain domain);

  UnitDomain findById(String id);

  PageResponse<UnitDomain> findAll(UnitCriteria criteria);

  UnitDomain update(String id, UnitDomain domain);
}
