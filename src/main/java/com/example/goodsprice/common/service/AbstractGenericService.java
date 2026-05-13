package com.example.goodsprice.common.service;

import com.example.goodsprice.common.dto.PageRequest;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractGenericService<T, ID> {

  private final String entityName;
  private final String notFoundErrorCode;

  protected AbstractGenericService(String entityName, String notFoundErrorCode) {
    this.entityName = entityName;
    this.notFoundErrorCode = notFoundErrorCode;
  }

  protected abstract GenericRepositoryPort<T, ID> getRepository();

  public T findById(ID id) {
    var entity = getRepository().findById(id);
    if (Objects.isNull(entity))
      throw new NotFoundException(
          notFoundErrorCode, "%s not found with id: %s".formatted(entityName, id));
    return entity;
  }

  public T save(T entity) {
    var saved = getRepository().save(entity);
    log.info("{} saved: {}", entityName, saved);
    return saved;
  }

  public PageResponse<T> findAll(PageRequest pageRequest, String search, String status) {
    return getRepository().findAll(pageRequest, search, status);
  }
}
