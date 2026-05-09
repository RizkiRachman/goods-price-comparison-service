package com.example.goodsprice.store.infrastructure.adapter.persistence;

import com.example.goodsprice.common.dto.PageRequest;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import com.example.goodsprice.store.infrastructure.adapter.persistence.entity.StoreEntity;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
    return jpaRepo.findAll().stream()
        .filter(e -> Objects.equals(e.getName(), name) && Objects.equals(e.getLocation(), location))
        .findFirst()
        .map(mapper::toDomain)
        .orElse(null);
  }

  @Override
  public boolean existsByNameAndLocation(String name, String location) {
    return jpaRepo.findAll().stream()
        .anyMatch(
            e -> Objects.equals(e.getName(), name) && Objects.equals(e.getLocation(), location));
  }

  @Override
  @CacheEvict(value = "stores", key = "#id")
  public void deleteById(Long id) {
    jpaRepo.deleteById(id);
  }

  @Override
  public PageResponse<StoreDomain> findAll(
      PageRequest pageRequest, String search, String status, String chain, String location) {
    var sort =
        Sort.by(
            "desc".equalsIgnoreCase(pageRequest.sortDirection())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC,
            pageRequest.sortBy());

    var pageNumber = Math.max(0, pageRequest.page() - 1);
    var pageable =
        org.springframework.data.domain.PageRequest.of(pageNumber, pageRequest.size(), sort);
    var spec = buildSpecification(search, status, chain, location);
    Page<StoreEntity> page = jpaRepo.findAll(spec, pageable);
    var stores = page.getContent().stream().map(mapper::toDomain).toList();
    return PageResponse.of(stores, pageRequest.page(), pageRequest.size(), page.getTotalElements());
  }

  private Specification<StoreEntity> buildSpecification(
      String search, String status, String chain, String location) {
    return (root, query, cb) -> {
      var predicates = new ArrayList<Predicate>();
      if (Objects.nonNull(search) && !search.isBlank()) {
        var pattern = "%%%s%%".formatted(search.toLowerCase(Locale.ROOT));
        predicates.add(
            cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("location")), pattern),
                cb.like(cb.lower(root.get("chain")), pattern),
                cb.like(cb.lower(root.get("address")), pattern)));
      }
      if (Objects.nonNull(status) && !status.isBlank()) {
        predicates.add(cb.equal(root.get("status"), status));
      }
      if (Objects.nonNull(chain) && !chain.isBlank()) {
        predicates.add(
            cb.like(
                cb.lower(root.get("chain")), "%%%s%%".formatted(chain.toLowerCase(Locale.ROOT))));
      }
      if (Objects.nonNull(location) && !location.isBlank()) {
        predicates.add(
            cb.like(
                cb.lower(root.get("location")),
                "%%%s%%".formatted(location.toLowerCase(Locale.ROOT))));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
