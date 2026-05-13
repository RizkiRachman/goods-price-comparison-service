package com.example.goodsprice.unit.application.port.out;

import com.example.goodsprice.common.dto.PageRequest;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import com.example.goodsprice.unit.application.domain.model.UnitDomain;

public interface UnitRepositoryPort extends GenericRepositoryPort<UnitDomain, String> {

  PageResponse<UnitDomain> findAll(
      PageRequest pageRequest, String search, String type, String status);
}
