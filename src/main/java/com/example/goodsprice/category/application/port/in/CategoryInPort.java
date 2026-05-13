package com.example.goodsprice.category.application.port.in;

import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.common.dto.PageResponse;

public interface CategoryInPort {

  CategoryDomain create(String id, String name, String description);

  CategoryDomain findById(String id);

  PageResponse<CategoryDomain> findAll(
      int page, int size, String sortBy, String sortDirection, String search, String status);

  CategoryDomain update(String id, String name, String description, String status);
}
