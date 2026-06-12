package com.example.goodsprice.store.infrastructure.adapter.persistence;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.persistence.PaginationHelper;
import com.example.goodsprice.common.persistence.SpecificationBuilder;
import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import com.example.goodsprice.config.CacheConfiguration;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.in.dto.StoreCriteria;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import com.example.goodsprice.store.infrastructure.adapter.persistence.entity.StoreEntity;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
  @CachePut(value = CacheConfiguration.STORES_CACHE, key = "#result.id")
  public StoreDomain save(StoreDomain store) {
    return super.save(store);
  }

  @Override
  @Cacheable(CacheConfiguration.STORES_CACHE)
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
  @CacheEvict(value = CacheConfiguration.STORES_CACHE, key = "#id")
  public void deleteById(Long id) {
    super.deleteById(id);
  }

  @Override
  public PageResponse<StoreDomain> findAll(StoreCriteria criteria) {
    var spec =
        new SpecificationBuilder<StoreEntity>()
            .addSearchLike(criteria.search(), "name", "location", "chain", "address")
            .addEqual("status", criteria.status())
            .addSearchLike(criteria.chain(), "chain")
            .addSearchLike(criteria.location(), "location")
            .build();
    return PaginationHelper.findAll(criteria.pageRequest(), spec, jpaRepo, mapper::toDomain);
  }
}
