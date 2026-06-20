package com.example.goodsprice.unit.infrastructure.adapter.persistence;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.persistence.PaginationHelper;
import com.example.goodsprice.common.persistence.SpecificationBuilder;
import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import com.example.goodsprice.config.CacheConfiguration;
import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import com.example.goodsprice.unit.application.port.in.dto.UnitCriteria;
import com.example.goodsprice.unit.application.port.out.UnitRepositoryPort;
import com.example.goodsprice.unit.infrastructure.adapter.persistence.entity.UnitEntity;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class UnitRepositoryAdapter extends AbstractRepositoryAdapter<UnitDomain, String, UnitEntity>
    implements UnitRepositoryPort {

  private final UnitMapper mapper;

  public UnitRepositoryAdapter(JpaUnitRepository jpaRepository, UnitMapper mapper) {
    super(jpaRepository, mapper::toEntity, mapper::toDomain);
    this.mapper = mapper;
  }

  @Override
  @CachePut(value = CacheConfiguration.UNITS_CACHE, key = "#result.id")
  public UnitDomain save(UnitDomain domain) {
    return super.save(domain);
  }

  @Override
  @Cacheable(CacheConfiguration.UNITS_CACHE)
  public UnitDomain findById(String id) {
    return super.findById(id);
  }

  @Override
  public PageResponse<UnitDomain> findAll(UnitCriteria criteria) {
    var spec =
        new SpecificationBuilder<UnitEntity>()
            .addSearchLike(criteria.search(), "name", "symbol", "id")
            .addEqualIgnoreCase("type", criteria.type())
            .addEqual("status", criteria.status())
            .build();
    return PaginationHelper.findAll(
        criteria.pageRequest(), spec, jpaSpecificationExecutor(), mapper::toDomain);
  }

  @Override
  public PageResponse<UnitDomain> findAll(
      PageRequestDto pageRequest, String search, String status) {
    var criteria = new UnitCriteria(pageRequest, search, null, status);
    return findAll(criteria);
  }
}
