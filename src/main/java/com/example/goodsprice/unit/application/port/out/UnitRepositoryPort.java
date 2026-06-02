package com.example.goodsprice.unit.application.port.out;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import com.example.goodsprice.unit.application.port.in.dto.UnitCriteria;

public interface UnitRepositoryPort extends GenericRepositoryPort<UnitDomain, String> {

  PageResponse<UnitDomain> findAll(UnitCriteria criteria);
}
