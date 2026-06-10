package com.example.goodsprice.store.infrastructure.adapter.persistence;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.persistence.PaginationHelper;
import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import com.example.goodsprice.common.util.SpecificationBuilder;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.in.dto.StoreCriteria;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import com.example.goodsprice.store.infrastructure.adapter.persistence.entity.StoreEntity;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StoreRepositoryAdapter
    extends AbstractRepositoryAdapter<StoreDomain, Long, StoreEntity>
    implements StoreRepositoryPort {

  private final JpaStoreRepository jpaRepo;
  private final StoreMapper mapper;

  public StoreRepositoryAdapter(JpaStoreRepository jpaRepo, StoreMapper mapper) {
    super(jpaRepo, mapper::toEntity, mapper::toDomain);
    this.jpaRepo = jpaRepo;
    this.mapper = mapper;
  }

  @Override
  @CachePut(value = "stores", key = "#result.id")
  public StoreDomain save(StoreDomain store) {
    return super.save(store);
  }

  @Override
  @Cacheable("stores")
  public StoreDomain findById(Long id) {
    return super.findById(id);
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
    super.deleteById(id);
  }

  @Override
  public PageResponse<StoreDomain> findAll(StoreCriteria criteria) {
    var spec = buildSpecification(criteria);
    return PaginationHelper.findAll(criteria.pageRequest(), spec, jpaRepo, mapper::toDomain);
  }

  private Specification<StoreEntity> buildSpecification(StoreCriteria criteria) {
    return (root, query, cb) -> {
      var predicates = new ArrayList<Predicate>();
      SpecificationBuilder.addSearchLike(
          predicates, root, cb, criteria.search(), "name", "location", "chain", "address");
      SpecificationBuilder.addEqual(predicates, root, cb, "status", criteria.status());
      SpecificationBuilder.addSearchLike(predicates, root, cb, criteria.chain(), "chain");
      SpecificationBuilder.addSearchLike(predicates, root, cb, criteria.location(), "location");
      return cb.and(SpecificationBuilder.toArray(predicates));
    };
  }
}
