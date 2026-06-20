package com.example.goodsprice.common.repository;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import java.util.List;
import java.util.function.Function;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

/**
 * Abstract base for repository adapters providing CRUD method implementations.
 *
 * <p>Subclasses provide the JPA repository and mapping functions via the constructor. Methods
 * {@link #save(Object)}, {@link #findById(Object)}, {@link #existsById(Object)}, and {@link
 * #deleteById(Object)} are implemented using these fields.
 *
 * <p>Adapters that need caching should override the relevant method(s) and delegate to {@code
 * super}.
 *
 * @param <T> domain type
 * @param <ID> ID type
 * @param <E> JPA entity type
 */
@Component
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractRepositoryAdapter<T, ID, E> {

  protected final JpaRepository<E, ID> jpaRepository;
  private final Function<T, E> toEntityFn;
  private final Function<E, T> toDomainFn;

  protected AbstractRepositoryAdapter(
      JpaRepository<E, ID> jpaRepository, Function<T, E> toEntity, Function<E, T> toDomain) {
    this.jpaRepository = jpaRepository;
    this.toEntityFn = toEntity;
    this.toDomainFn = toDomain;
  }

  public T save(T domain) {
    var entity = toEntityFn.apply(domain);
    var saved = jpaRepository.save(entity);
    return toDomainFn.apply(saved);
  }

  public List<T> saveAll(Iterable<T> domains) {
    var entities = new java.util.ArrayList<E>();
    domains.forEach(d -> entities.add(toEntityFn.apply(d)));
    var saved = jpaRepository.saveAll(entities);
    return saved.stream().map(toDomainFn::apply).toList();
  }

  public T findById(ID id) {
    return jpaRepository.findById(id).map(toDomainFn::apply).orElse(null);
  }

  public boolean existsById(ID id) {
    return jpaRepository.existsById(id);
  }

  public void deleteById(ID id) {
    jpaRepository.deleteById(id);
  }

  @Deprecated(forRemoval = true)
  public List<T> findAll() {
    LoggerFactory.getLogger(getClass())
        .warn("Unbounded findAll() called - this may cause performance issues for large datasets");
    return jpaRepository.findAll().stream().map(toDomainFn::apply).toList();
  }

  @SuppressWarnings("unchecked")
  protected JpaSpecificationExecutor<E> jpaSpecificationExecutor() {
    return (JpaSpecificationExecutor<E>) jpaRepository;
  }

  public PageResponse<T> findAll(PageRequestDto pageRequest, String search, String status) {
    var pageable = PageRequest.of(pageRequest.toZeroBased(), pageRequest.size());
    var page = jpaRepository.findAll(pageable);
    var domains = page.getContent().stream().map(toDomainFn::apply).toList();
    return PageResponse.of(
        domains, pageRequest.page(), pageRequest.size(), page.getTotalElements());
  }
}
