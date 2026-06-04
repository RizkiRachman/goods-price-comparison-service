package com.example.goodsprice.unit.application.domain.service;

import com.example.goodsprice.activity.application.annotation.ActivityLog;
import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.service.AbstractGenericService;
import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import com.example.goodsprice.unit.application.domain.model.UnitType;
import com.example.goodsprice.unit.application.port.in.UnitInPort;
import com.example.goodsprice.unit.application.port.in.dto.UnitCriteria;
import com.example.goodsprice.unit.application.port.out.UnitRepositoryPort;
import java.util.Locale;
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
  public UnitDomain create(String id, String name, String symbol, String type, String description) {
    var unit =
        UnitDomain.builder()
            .id(id)
            .name(name)
            .symbol(symbol)
            .type(UnitType.valueOf(type.toUpperCase(Locale.ROOT)))
            .description(description)
            .status("ACTIVE")
            .build();
    return save(unit);
  }

  @Override
  public PageResponse<UnitDomain> findAll(UnitCriteria criteria) {
    return unitRepository.findAll(criteria);
  }

  @Override
  @Transactional
  @ActivityLog
  public UnitDomain update(
      String id, String name, String symbol, String type, String description, String status) {
    var existing = findById(id);
    existing.setName(name);
    existing.setSymbol(symbol);
    if (type != null) {
      existing.setType(UnitType.valueOf(type.toUpperCase(Locale.ROOT)));
    }
    existing.setDescription(description);
    existing.setStatus(status);
    return save(existing);
  }
}
