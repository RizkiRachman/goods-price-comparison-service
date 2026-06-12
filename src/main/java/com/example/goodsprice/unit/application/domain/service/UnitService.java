package com.example.goodsprice.unit.application.domain.service;

import com.example.goodsprice.activity.application.annotation.ActivityLog;
import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.service.AbstractGenericService;
import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import com.example.goodsprice.unit.application.port.in.UnitInPort;
import com.example.goodsprice.unit.application.port.in.dto.UnitCriteria;
import com.example.goodsprice.unit.application.port.out.UnitRepositoryPort;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UnitService extends AbstractGenericService<UnitDomain, String> implements UnitInPort {

  private final UnitRepositoryPort unitRepository;

  public UnitService(UnitRepositoryPort unitRepository) {
    super("Unit", ErrorCodes.UNIT_NOT_FOUND);
    this.unitRepository = unitRepository;
  }

  @Override
  protected UnitRepositoryPort getRepository() {
    return unitRepository;
  }

  @Override
  @Transactional
  @ActivityLog
  public UnitDomain create(UnitDomain domain) {
    domain.setStatus("ACTIVE");
    return save(domain);
  }

  @Override
  public PageResponse<UnitDomain> findAll(UnitCriteria criteria) {
    return unitRepository.findAll(criteria);
  }

  @Override
  @Transactional
  @ActivityLog
  public UnitDomain update(String id, UnitDomain domain) {
    var existing = findById(id);
    existing.setName(domain.getName());
    existing.setSymbol(domain.getSymbol());
    if (Objects.nonNull(domain.getType())) {
      existing.setType(domain.getType());
    }
    existing.setDescription(domain.getDescription());
    existing.setStatus(domain.getStatus());
    return save(existing);
  }
}
