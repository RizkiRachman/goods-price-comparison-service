package com.example.goodsprice.activity.infrastructure.adapter.persistence;

import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.DEFAULT_SORT_FIELD;
import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.ENTITY_FIELD_ACTION;
import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.ENTITY_FIELD_TYPE;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import com.example.goodsprice.activity.application.port.out.ActivityLogRepositoryPort;
import com.example.goodsprice.activity.infrastructure.adapter.persistence.entity.ActivityLogEntity;
import com.example.goodsprice.common.constant.SortConstants;
import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityLogRepositoryAdapter implements ActivityLogRepositoryPort {

  private final JpaActivityLogRepository jpaRepository;
  private final ActivityLogMapper mapper;

  @Override
  public ActivityLogDomain save(ActivityLogDomain domain) {
    var entity = mapper.toEntity(domain);
    var saved = jpaRepository.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public ActivityLogDomain findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain).orElse(null);
  }

  @Override
  public boolean existsById(UUID id) {
    return jpaRepository.existsById(id);
  }

  @Override
  public void deleteById(UUID id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public PageResponse<ActivityLogDomain> findAll(
      PageRequestDto pageRequest, String search, String status) {
    if (Objects.nonNull(search) && !search.isBlank()) {
      var spec = searchSpecification(search);
      return executeQuery(pageRequest, spec);
    }
    return findAll(pageRequest, null, null, null, null);
  }

  @Override
  public PageResponse<ActivityLogDomain> findAll(
      PageRequestDto pageRequest,
      ActivityLogType type,
      ActivityLogAction action,
      LocalDateTime startDate,
      LocalDateTime endDate) {
    var spec = buildSpecification(type, action, startDate, endDate);
    return executeQuery(pageRequest, spec);
  }

  private PageResponse<ActivityLogDomain> executeQuery(
      PageRequestDto pageRequest, Specification<ActivityLogEntity> spec) {
    var sortBy =
        Objects.nonNull(pageRequest.sortBy()) && !pageRequest.sortBy().isBlank()
            ? pageRequest.sortBy()
            : DEFAULT_SORT_FIELD;
    var sort =
        Sort.by(
            SortConstants.DESC.equalsIgnoreCase(pageRequest.sortDirection())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC,
            sortBy);

    var pageable =
        org.springframework.data.domain.PageRequest.of(
            pageRequest.toZeroBased(), pageRequest.size(), sort);

    Page<ActivityLogEntity> page = jpaRepository.findAll(spec, pageable);
    var domains = page.getContent().stream().map(mapper::toDomain).toList();
    return PageResponse.of(
        domains, pageRequest.page(), pageRequest.size(), page.getTotalElements());
  }

  private Specification<ActivityLogEntity> buildSpecification(
      ActivityLogType type,
      ActivityLogAction action,
      LocalDateTime startDate,
      LocalDateTime endDate) {
    return (root, query, cb) -> {
      var predicates = new ArrayList<Predicate>();
      if (Objects.nonNull(type)) {
        predicates.add(cb.equal(root.get(ENTITY_FIELD_TYPE), type.name()));
      }
      if (Objects.nonNull(action)) {
        predicates.add(cb.equal(root.get(ENTITY_FIELD_ACTION), action.name()));
      }
      if (Objects.nonNull(startDate)) {
        predicates.add(cb.greaterThanOrEqualTo(root.get(DEFAULT_SORT_FIELD), startDate));
      }
      if (Objects.nonNull(endDate)) {
        predicates.add(cb.lessThanOrEqualTo(root.get(DEFAULT_SORT_FIELD), endDate));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private Specification<ActivityLogEntity> searchSpecification(String search) {
    return (root, query, cb) -> {
      var pattern = "%" + search.toLowerCase(Locale.ROOT) + "%";
      return cb.like(cb.lower(root.get("description")), pattern);
    };
  }
}
