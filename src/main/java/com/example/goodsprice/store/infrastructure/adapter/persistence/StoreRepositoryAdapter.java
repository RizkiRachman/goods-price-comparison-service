package com.example.goodsprice.store.infrastructure.adapter.persistence;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.persistence.PaginationHelper;
import com.example.goodsprice.common.util.SpecificationBuilder;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import com.example.goodsprice.store.infrastructure.adapter.persistence.entity.StoreEntity;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoreRepositoryAdapter implements StoreRepositoryPort {

  private final JpaStoreRepository jpaRepo;
  private final StoreMapper mapper;

  @Override
  @CachePut(value = "stores", key = "#result.id")
  public StoreDomain save(StoreDomain store) {
    var entity = mapper.toEntity(store);
    var saved = jpaRepo.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  @Cacheable("stores")
  public StoreDomain findById(Long id) {
    return jpaRepo.findById(id).map(mapper::toDomain).orElse(null);
  }

  @Override
  public List<StoreDomain> findByName(String name) {
    return jpaRepo.findByName(name).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<StoreDomain> findAllById(List<Long> ids) {
    return jpaRepo.findAllById(ids).stream().map(mapper::toDomain).toList();
  }

  @Override
  public StoreDomain findByNameAndLocation(String name, String location) {
    return jpaRepo.findByNameAndLocation(name, location).map(mapper::toDomain).orElse(null);
  }

  @Override
  public boolean existsByNameAndLocation(String name, String location) {
    return jpaRepo.existsByNameAndLocation(name, location);
  }

  @Override
  @CacheEvict(value = "stores", key = "#id")
  public void deleteById(Long id) {
    jpaRepo.deleteById(id);
  }

  @Override
  public PageResponse<StoreDomain> findAll(
      PageRequestDto pageRequest, String search, String status, String chain, String location) {
    var spec = buildSpecification(search, status, chain, location);
    return PaginationHelper.findAll(pageRequest, spec, jpaRepo, mapper::toDomain);
  }

  private Specification<StoreEntity> buildSpecification(
      String search, String status, String chain, String location) {
    return (root, query, cb) -> {
      var predicates = new ArrayList<Predicate>();
      SpecificationBuilder.addSearchLike(
          predicates, root, cb, search, "name", "location", "chain", "address");
      SpecificationBuilder.addEqual(predicates, root, cb, "status", status);
      SpecificationBuilder.addSearchLike(predicates, root, cb, chain, "chain");
      SpecificationBuilder.addSearchLike(predicates, root, cb, location, "location");
      return cb.and(SpecificationBuilder.toArray(predicates));
    };
  }
}
