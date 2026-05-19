package com.example.goodsprice.activity.application.domain.service;

import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.port.in.ActivityLogInPort;
import com.example.goodsprice.activity.application.port.out.ActivityLogRepositoryPort;
import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.dto.PageRequest;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.service.AbstractGenericService;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ActivityLogService extends AbstractGenericService<ActivityLogDomain, UUID>
    implements ActivityLogInPort {

  private final ActivityLogRepositoryPort activityLogRepository;

  public ActivityLogService(ActivityLogRepositoryPort activityLogRepository) {
    super("ActivityLog", ErrorCodes.ACTIVITY_LOG_NOT_FOUND);
    this.activityLogRepository = activityLogRepository;
  }

  @Override
  protected ActivityLogRepositoryPort getRepository() {
    return activityLogRepository;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void log(ActivityLogDomain activity) {
    getRepository().save(activity);
    log.debug("Activity logged: {} {}", activity.getType(), activity.getDescription());
  }

  @Override
  public PageResponse<ActivityLogDomain> findAll(
      int page,
      int size,
      String sortBy,
      String sortDirection,
      String type,
      String action,
      OffsetDateTime startDate,
      OffsetDateTime endDate) {
    var pageRequest = new PageRequest(page, size, sortBy, sortDirection);
    var start = Objects.nonNull(startDate) ? startDate.toLocalDateTime() : null;
    var end = Objects.nonNull(endDate) ? endDate.toLocalDateTime() : null;
    return activityLogRepository.findAll(pageRequest, type, action, start, end);
  }
}
