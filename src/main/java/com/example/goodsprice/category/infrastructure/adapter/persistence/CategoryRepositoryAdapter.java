package com.example.goodsprice.category.infrastructure.adapter.persistence;

import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.application.port.out.CategoryRepositoryPort;
import com.example.goodsprice.category.infrastructure.adapter.persistence.entity.CategoryEntity;
import com.example.goodsprice.common.dto.PageRequest;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.util.SpecificationBuilder;
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
    var pageable =
        org.springframework.data.domain.PageRequest.of(
            pageRequest.toZeroBased(), pageRequest.size(), sort);
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

  @Override
  public void deleteById(String id) {
    jpaRepo.deleteById(id);
  }

  private Specification<CategoryEntity> buildSpecification(String search, String status) {
    return (root, query, cb) -> {
      var predicates = new ArrayList<Predicate>();
      SpecificationBuilder.addSearchLike(predicates, root, cb, search, "name", "id");
      SpecificationBuilder.addEqual(predicates, root, cb, "status", status);
      return cb.and(SpecificationBuilder.toArray(predicates));
    };
  }
}
