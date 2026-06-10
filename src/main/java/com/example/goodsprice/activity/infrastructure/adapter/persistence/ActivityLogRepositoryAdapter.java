package com.example.goodsprice.activity.infrastructure.adapter.persistence;

import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.DEFAULT_SORT_FIELD;
import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.ENTITY_FIELD_ACTION;
import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.ENTITY_FIELD_TYPE;

import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.port.in.dto.ActivityLogCriteria;
import com.example.goodsprice.activity.application.port.out.ActivityLogRepositoryPort;
import com.example.goodsprice.activity.infrastructure.adapter.persistence.entity.ActivityLogEntity;
import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.persistence.PaginationHelper;
import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import com.example.goodsprice.common.util.ObjectUtils;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogRepositoryAdapter
    extends AbstractRepositoryAdapter<ActivityLogDomain, UUID, ActivityLogEntity>
    implements ActivityLogRepositoryPort {

  private final JpaActivityLogRepository jpaRepository;
  private final ActivityLogMapper mapper;

  public ActivityLogRepositoryAdapter(
      JpaActivityLogRepository jpaRepository, ActivityLogMapper mapper) {
    super(jpaRepository, mapper::toEntity, mapper::toDomain);
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public PageResponse<ActivityLogDomain> findAll(ActivityLogCriteria criteria) {
    var spec = buildSpecification(criteria);
    return executeQuery(criteria.pageRequest(), spec);
  }

  @Override
  public PageResponse<ActivityLogDomain> findAll(
      PageRequestDto pageRequest, String search, String status) {
    // Delegate to the criteria-based findAll for compatibility with GenericRepositoryPort
    return findAll(new ActivityLogCriteria(pageRequest, null, null, null, null));
  }

  private PageResponse<ActivityLogDomain> executeQuery(
      PageRequestDto pageRequest, Specification<ActivityLogEntity> spec) {
    var actualSortBy = ObjectUtils.defaultIfNull(pageRequest.sortBy(), DEFAULT_SORT_FIELD);
    var pr =
        new PageRequestDto(
            pageRequest.page(), pageRequest.size(), actualSortBy, pageRequest.sortDirection());
    return PaginationHelper.findAll(pr, spec, jpaRepository, mapper::toDomain);
  }

  private Specification<ActivityLogEntity> buildSpecification(ActivityLogCriteria criteria) {
    return (root, query, cb) -> {
      var predicates = new ArrayList<Predicate>();
      if (Objects.nonNull(criteria.type())) {
        predicates.add(cb.equal(root.get(ENTITY_FIELD_TYPE), criteria.type().name()));
      }
      if (Objects.nonNull(criteria.action())) {
        predicates.add(cb.equal(root.get(ENTITY_FIELD_ACTION), criteria.action().name()));
      }
      if (Objects.nonNull(criteria.startDate())) {
        predicates.add(cb.greaterThanOrEqualTo(root.get(DEFAULT_SORT_FIELD), criteria.startDate()));
      }
      if (Objects.nonNull(criteria.endDate())) {
        predicates.add(cb.lessThanOrEqualTo(root.get(DEFAULT_SORT_FIELD), criteria.endDate()));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
