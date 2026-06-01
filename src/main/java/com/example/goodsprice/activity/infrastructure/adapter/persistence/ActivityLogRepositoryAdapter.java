package com.example.goodsprice.activity.infrastructure.adapter.persistence;

import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.DEFAULT_SORT_FIELD;
import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.ENTITY_FIELD_ACTION;
import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.ENTITY_FIELD_TYPE;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import com.example.goodsprice.activity.application.port.out.ActivityLogRepositoryPort;
import com.example.goodsprice.activity.infrastructure.adapter.persistence.entity.ActivityLogEntity;
import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.persistence.PaginationHelper;
import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import com.example.goodsprice.common.util.ObjectUtils;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogRepositoryAdapter
    extends AbstractRepositoryAdapter<ActivityLogDomain, UUID, ActivityLogEntity>
    implements ActivityLogRepositoryPort {

  private final JpaActivityLogRepository jpaRepository;
  private final ActivityLogMapper mapper;

  public ActivityLogRepositoryAdapter(
      JpaActivityLogRepository jpaRepository, ActivityLogMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  protected JpaRepository<ActivityLogEntity, UUID> getJpaRepository() {
    return jpaRepository;
  }

  @Override
  protected ActivityLogEntity toEntity(ActivityLogDomain domain) {
    return mapper.toEntity(domain);
  }

  @Override
  protected ActivityLogDomain toDomain(ActivityLogEntity entity) {
    return mapper.toDomain(entity);
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
    var actualSortBy = ObjectUtils.defaultIfNull(pageRequest.sortBy(), DEFAULT_SORT_FIELD);
    var pr =
        new PageRequestDto(
            pageRequest.page(), pageRequest.size(), actualSortBy, pageRequest.sortDirection());
    return PaginationHelper.findAll(pr, spec, jpaRepository, mapper::toDomain);
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
