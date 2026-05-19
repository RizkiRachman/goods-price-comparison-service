package com.example.goodsprice.activity.infrastructure.adapter.persistence;

import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.port.out.ActivityLogRepositoryPort;
import com.example.goodsprice.activity.infrastructure.adapter.persistence.entity.ActivityLogEntity;
import com.example.goodsprice.common.dto.PageRequest;
import com.example.goodsprice.common.dto.PageResponse;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
  public PageResponse<ActivityLogDomain> findAll(
      PageRequest pageRequest, String search, String status) {
    return findAll(pageRequest, search, status, null, null);
  }

  @Override
  public PageResponse<ActivityLogDomain> findAll(
      PageRequest pageRequest,
      String type,
      String action,
      LocalDateTime startDate,
      LocalDateTime endDate) {
    var sortBy =
        Objects.nonNull(pageRequest.sortBy()) && !pageRequest.sortBy().isBlank()
            ? pageRequest.sortBy()
            : "createdAt";
    var sort =
        Sort.by(
            "desc".equalsIgnoreCase(pageRequest.sortDirection())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC,
            sortBy);

    var pageNumber = Math.max(0, pageRequest.page() - 1);
    var pageable =
        org.springframework.data.domain.PageRequest.of(pageNumber, pageRequest.size(), sort);

    Specification<ActivityLogEntity> spec =
        (root, query, cb) -> {
          var predicates = new ArrayList<Predicate>();
          if (Objects.nonNull(type) && !type.isBlank()) {
            predicates.add(cb.equal(root.get("type"), type));
          }
          if (Objects.nonNull(action) && !action.isBlank()) {
            predicates.add(cb.equal(root.get("action"), action));
          }
          if (Objects.nonNull(startDate)) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
          }
          if (Objects.nonNull(endDate)) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
          }
          return cb.and(predicates.toArray(new Predicate[0]));
        };

    Page<ActivityLogEntity> page = jpaRepository.findAll(spec, pageable);
    var domains = page.getContent().stream().map(mapper::toDomain).toList();
    return PageResponse.of(
        domains, pageRequest.page(), pageRequest.size(), page.getTotalElements());
  }
}
