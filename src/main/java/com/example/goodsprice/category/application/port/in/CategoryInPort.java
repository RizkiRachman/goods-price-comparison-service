package com.example.goodsprice.category.application.port.in;

import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.application.port.in.dto.CategoryCriteria;
import com.example.goodsprice.common.dto.PageResponse;

public interface CategoryInPort {

  CategoryDomain create(String id, String name, String description);

  CategoryDomain findById(String id);

  PageResponse<CategoryDomain> findAll(CategoryCriteria criteria);

  CategoryDomain update(String id, String name, String description, String status);
}
