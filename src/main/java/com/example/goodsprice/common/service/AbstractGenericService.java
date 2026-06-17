package com.example.goodsprice.common.service;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import java.util.Objects;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

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
    Objects.requireNonNull(id, "id must not be null");
    var entity = getRepository().findById(id);
    if (Objects.isNull(entity))
      throw new NotFoundException(
          notFoundErrorCode, "%s not found with id: %s".formatted(entityName, id));
    return entity;
  }

  public T save(T entity) {
    var saved = getRepository().save(entity);
    log.debug("{} saved", entityName);
    return saved;
  }

  /**
   * Generic update template: finds entity by ID, applies field updates via a merger function,
   * saves, and logs. Throws NotFoundException if not found.
   *
   * <p>Usage: subclass calls {@code update(id, (existing, update) -> {
   * existing.setName(update.getName()); ... }, updateWith)}
   *
   * @param <U> the type of the object containing updated field values
   * @param id entity ID to update
   * @param merger function that copies fields from updateWith into existing entity
   * @param updateWith the object containing updated field values
   * @return the updated and saved entity
   */
  @Transactional
  public <U> T update(ID id, BiConsumer<T, U> merger, U updateWith) {
    var existing = findById(id); // throws NotFoundException if not found
    merger.accept(existing, updateWith);
    var saved = save(existing);
    log.info("{} updated: {}", entityName, id);
    return saved;
  }

  @Deprecated(forRemoval = true)
  public PageResponse<T> findAll(PageRequestDto pageRequest, String search, String status) {
    return getRepository().findAll(pageRequest, search, status);
  }

  @Transactional
  public void deleteById(ID id) {
    findById(id); // throws NotFoundException if not found
    getRepository().deleteById(id);
    log.info("{} deleted with id: {}", entityName, id);
  }
}
