package com.example.goodsprice.category.infrastructure.adapter.persistence;

import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.application.port.out.CategoryRepositoryPort;
import com.example.goodsprice.category.infrastructure.adapter.persistence.entity.CategoryEntity;
import com.example.goodsprice.common.dto.PageRequest;
import com.example.goodsprice.common.dto.PageResponse;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

  private final JpaCategoryRepository jpaRepo;
  private final CategoryMapper mapper;

  @Override
  @CachePut(value = "categories", key = "#result.id")
  public CategoryDomain save(CategoryDomain domain) {
    var entity = mapper.toEntity(domain);
    var saved = jpaRepo.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  @Cacheable("categories")
  public CategoryDomain findById(String id) {
    return jpaRepo.findById(id).map(mapper::toDomain).orElse(null);
  }

  @Override
  public PageResponse<CategoryDomain> findAll(
      PageRequest pageRequest, String search, String status) {
    var sort =
        Sort.by(
            "desc".equalsIgnoreCase(pageRequest.sortDirection())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC,
            pageRequest.sortBy());
    var pageNumber = Math.max(0, pageRequest.page() - 1);
    var pageable =
        org.springframework.data.domain.PageRequest.of(pageNumber, pageRequest.size(), sort);
    var spec = buildSpecification(search, status);
    Page<CategoryEntity> page = jpaRepo.findAll(spec, pageable);
    var categories = page.getContent().stream().map(mapper::toDomain).toList();
    return PageResponse.of(
        categories, pageRequest.page(), pageRequest.size(), page.getTotalElements());
  }

  @Override
  public boolean existsById(String id) {
    return jpaRepo.existsById(id);
  }

  private Specification<CategoryEntity> buildSpecification(String search, String status) {
    return (root, query, cb) -> {
      var predicates = new ArrayList<Predicate>();
      if (Objects.nonNull(search) && !search.isBlank()) {
        var pattern = "%%%s%%".formatted(search.toLowerCase(Locale.ROOT));
        predicates.add(
            cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("id")), pattern)));
      }
      if (Objects.nonNull(status) && !status.isBlank()) {
        predicates.add(cb.equal(root.get("status"), status));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
