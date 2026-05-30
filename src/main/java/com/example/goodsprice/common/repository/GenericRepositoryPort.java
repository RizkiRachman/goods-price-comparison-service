package com.example.goodsprice.common.repository;

import com.example.goodsprice.common.dto.PageRequest;
import com.example.goodsprice.common.dto.PageResponse;

public interface GenericRepositoryPort<T, ID> {

  T save(T domain);

  T findById(ID id);

  PageResponse<T> findAll(PageRequest pageRequest, String search, String status);

  boolean existsById(ID id);

  void deleteById(ID id);
}
