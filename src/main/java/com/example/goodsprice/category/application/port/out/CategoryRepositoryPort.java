package com.example.goodsprice.category.application.port.out;

import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.application.port.in.dto.CategoryCriteria;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.repository.GenericRepositoryPort;

public interface CategoryRepositoryPort extends GenericRepositoryPort<CategoryDomain, String> {
  PageResponse<CategoryDomain> findAll(CategoryCriteria criteria);
}
