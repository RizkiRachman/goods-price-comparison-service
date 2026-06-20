package com.example.goodsprice.category.infrastructure.adapter.persistence;

import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.application.port.in.dto.CategoryCriteria;
import com.example.goodsprice.category.application.port.out.CategoryRepositoryPort;
import com.example.goodsprice.category.infrastructure.adapter.persistence.entity.CategoryEntity;
import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.persistence.PaginationHelper;
import com.example.goodsprice.common.persistence.SpecificationBuilder;
import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import com.example.goodsprice.config.CacheConfiguration;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class CategoryRepositoryAdapter
    extends AbstractRepositoryAdapter<CategoryDomain, String, CategoryEntity>
    implements CategoryRepositoryPort {

  private final CategoryMapper mapper;

  public CategoryRepositoryAdapter(JpaCategoryRepository jpaRepository, CategoryMapper mapper) {
    super(jpaRepository, mapper::toEntity, mapper::toDomain);
    this.mapper = mapper;
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
  public PageResponse<CategoryDomain> findAll(CategoryCriteria criteria) {
    var spec =
        new SpecificationBuilder<CategoryEntity>()
            .addSearchLike(criteria.search(), "name", "id")
            .addEqual("status", criteria.status())
            .build();
    return PaginationHelper.findAll(
        criteria.pageRequest(), spec, jpaSpecificationExecutor(), mapper::toDomain);
  }

  @Override
  public PageResponse<CategoryDomain> findAll(
      PageRequestDto pageRequest, String search, String status) {
    return findAll(new CategoryCriteria(pageRequest, search, status));
  }
}
