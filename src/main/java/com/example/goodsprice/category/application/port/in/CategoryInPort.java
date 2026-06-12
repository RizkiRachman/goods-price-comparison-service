package com.example.goodsprice.category.application.port.in;

import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.application.port.in.dto.CategoryCriteria;
import com.example.goodsprice.common.dto.PageResponse;

public interface CategoryInPort {

  CategoryDomain create(CategoryDomain domain);

  CategoryDomain findById(String id);

  PageResponse<CategoryDomain> findAll(CategoryCriteria criteria);

  CategoryDomain update(String id, CategoryDomain domain);
}
