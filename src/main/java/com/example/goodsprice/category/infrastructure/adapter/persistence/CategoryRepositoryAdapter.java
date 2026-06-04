package com.example.goodsprice.category.infrastructure.adapter.persistence;

import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.application.port.in.dto.CategoryCriteria;
import com.example.goodsprice.category.application.port.out.CategoryRepositoryPort;
import com.example.goodsprice.category.infrastructure.adapter.persistence.entity.CategoryEntity;
import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.persistence.PaginationHelper;
import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import com.example.goodsprice.common.util.SpecificationBuilder;
import com.example.goodsprice.config.CacheConfiguration;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class CategoryRepositoryAdapter
    extends AbstractRepositoryAdapter<CategoryDomain, String, CategoryEntity>
    implements CategoryRepositoryPort {

  private final JpaCategoryRepository jpaRepo;
  private final CategoryMapper mapper;

  public CategoryRepositoryAdapter(JpaCategoryRepository jpaRepo, CategoryMapper mapper) {
    this.jpaRepo = jpaRepo;
    this.mapper = mapper;
  }

  @Override
  protected JpaRepository<CategoryEntity, String> getJpaRepository() {
    return jpaRepo;
  }

  @Override
  protected CategoryEntity toEntity(CategoryDomain domain) {
    return mapper.toEntity(domain);
  }

  @Override
  protected CategoryDomain toDomain(CategoryEntity entity) {
    return mapper.toDomain(entity);
  }

  @Override
  @CachePut(value = CacheConfiguration.CATEGORIES_CACHE, key = "#result.id")
  public CategoryDomain save(CategoryDomain domain) {
    return super.save(domain);
  }

  @Override
  @Cacheable(CacheConfiguration.CATEGORIES_CACHE)
  public CategoryDomain findById(String id) {
    return super.findById(id);
  }

  @Override
  public PageResponse<CategoryDomain> findAll(
      PageRequestDto pageRequest, String search, String status) {
    var spec = buildSpecification(search, status);
    return PaginationHelper.findAll(pageRequest, spec, jpaRepo, mapper::toDomain);
  }

  @Override
  public PageResponse<CategoryDomain> findAll(CategoryCriteria criteria) {
    return findAll(criteria.pageRequest(), criteria.search(), criteria.status());
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
