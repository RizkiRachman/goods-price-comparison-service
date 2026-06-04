package com.example.goodsprice.category.application.domain.service;

import com.example.goodsprice.activity.application.annotation.ActivityLog;
import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.application.port.in.CategoryInPort;
import com.example.goodsprice.category.application.port.in.dto.CategoryCriteria;
import com.example.goodsprice.category.application.port.out.CategoryRepositoryPort;
import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.service.AbstractGenericService;
import com.example.goodsprice.config.CacheConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CategoryService extends AbstractGenericService<CategoryDomain, String>
    implements CategoryInPort {

  private final CategoryRepositoryPort categoryRepository;

  public CategoryService(CategoryRepositoryPort categoryRepository) {
    super("Category", ErrorCodes.CATEGORY_NOT_FOUND);
    this.categoryRepository = categoryRepository;
  }

  @Override
  protected CategoryRepositoryPort getRepository() {
    return categoryRepository;
  }

  @Override
  @Transactional
  @ActivityLog
  @CacheEvict(value = CacheConfiguration.CATEGORIES_CACHE, allEntries = true)
  public CategoryDomain create(String id, String name, String description) {
    var category =
        CategoryDomain.builder()
            .id(id)
            .name(name)
            .description(description)
            .status("ACTIVE")
            .build();
    return save(category);
  }

  @Override
  public PageResponse<CategoryDomain> findAll(CategoryCriteria criteria) {
    return findAll(criteria.pageRequest(), criteria.search(), criteria.status());
  }

  @Override
  @Transactional
  @ActivityLog
  @CacheEvict(value = CacheConfiguration.CATEGORIES_CACHE, allEntries = true)
  public CategoryDomain update(String id, String name, String description, String status) {
    var existing = findById(id);
    existing.setName(name);
    existing.setDescription(description);
    existing.setStatus(status);
    return save(existing);
  }
}
