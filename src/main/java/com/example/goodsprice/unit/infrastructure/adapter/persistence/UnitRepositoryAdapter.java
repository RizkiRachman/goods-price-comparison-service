package com.example.goodsprice.unit.infrastructure.adapter.persistence;

import com.example.goodsprice.common.dto.PageRequest;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.util.SpecificationBuilder;
import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import com.example.goodsprice.unit.application.port.out.UnitRepositoryPort;
import com.example.goodsprice.unit.infrastructure.adapter.persistence.entity.UnitEntity;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnitRepositoryAdapter implements UnitRepositoryPort {

  private final JpaUnitRepository jpaRepo;
  private final UnitMapper mapper;

  @Override
  @CachePut(value = "units", key = "#result.id")
  public UnitDomain save(UnitDomain domain) {
    var entity = mapper.toEntity(domain);
    var saved = jpaRepo.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  @Cacheable("units")
  public UnitDomain findById(String id) {
    return jpaRepo.findById(id).map(mapper::toDomain).orElse(null);
  }

  @Override
  public PageResponse<UnitDomain> findAll(
      PageRequest pageRequest, String search, String type, String status) {
    var sort =
        Sort.by(
            "desc".equalsIgnoreCase(pageRequest.sortDirection())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC,
            pageRequest.sortBy());
    var pageable =
        org.springframework.data.domain.PageRequest.of(
            pageRequest.toZeroBased(), pageRequest.size(), sort);
    var spec = buildSpecification(search, type, status);
    Page<UnitEntity> page = jpaRepo.findAll(spec, pageable);
    var units = page.getContent().stream().map(mapper::toDomain).toList();
    return PageResponse.of(units, pageRequest.page(), pageRequest.size(), page.getTotalElements());
  }

  @Override
  public boolean existsById(String id) {
    return jpaRepo.existsById(id);
  }

  private Specification<UnitEntity> buildSpecification(String search, String type, String status) {
    return (root, query, cb) -> {
      var predicates = new ArrayList<Predicate>();
      SpecificationBuilder.addSearchLike(predicates, root, cb, search, "name", "symbol", "id");
      SpecificationBuilder.addEqualIgnoreCase(predicates, root, cb, "type", type);
      SpecificationBuilder.addEqual(predicates, root, cb, "status", status);
      return cb.and(SpecificationBuilder.toArray(predicates));
    };
  }

  @Override
  public PageResponse<UnitDomain> findAll(PageRequest pageRequest, String search, String status) {
    return findAll(pageRequest, search, null, status);
  }
}
