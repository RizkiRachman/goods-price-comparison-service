package com.example.goodsprice.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * Abstract base for repository adapters providing CRUD method implementations.
 *
 * <p>Subclasses provide entity↔domain mapping via {@link #toEntity(Object)} and {@link
 * #toDomain(Object)}, and the JPA repository via {@link #getJpaRepository()}. Methods {@link
 * #save(Object)}, {@link #findById(Object)}, {@link #existsById(Object)}, and {@link
 * #deleteById(Object)} are implemented using these three abstract methods.
 *
 * <p>Adapters that need caching should override the relevant method(s) and delegate to {@code
 * super}.
 *
 * @param <T> domain type
 * @param <ID> ID type
 * @param <E> JPA entity type
 */
@Component
public abstract class AbstractRepositoryAdapter<T, ID, E> {

  protected abstract JpaRepository<E, ID> getJpaRepository();

  protected abstract E toEntity(T domain);

  protected abstract T toDomain(E entity);

  public T save(T domain) {
    var entity = toEntity(domain);
    var saved = getJpaRepository().save(entity);
    return toDomain(saved);
  }

  public T findById(ID id) {
    return getJpaRepository().findById(id).map(this::toDomain).orElse(null);
  }

  public boolean existsById(ID id) {
    return getJpaRepository().existsById(id);
  }

  public void deleteById(ID id) {
    getJpaRepository().deleteById(id);
  }
}
