package com.example.goodsprice.activity.application.domain.service;

import static com.example.goodsprice.activity.infrastructure.config.ActivityLogConstants.ENTITY_NAME;

import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.port.in.ActivityLogInPort;
import com.example.goodsprice.activity.application.port.in.dto.ActivityLogCriteria;
import com.example.goodsprice.activity.application.port.out.ActivityLogRepositoryPort;
import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.service.AbstractGenericService;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ActivityLogService extends AbstractGenericService<ActivityLogDomain, UUID>
    implements ActivityLogInPort {

  private final ActivityLogRepositoryPort activityLogRepository;

  public ActivityLogService(ActivityLogRepositoryPort activityLogRepository) {
    super(ENTITY_NAME, ErrorCodes.ACTIVITY_LOG_NOT_FOUND);
    this.activityLogRepository = activityLogRepository;
  }

  @Override
  protected ActivityLogRepositoryPort getRepository() {
    return activityLogRepository;
  }

  @Override
  @Transactional
  public void log(ActivityLogDomain activity) {
    getRepository().save(activity);
    log.debug("Activity logged: {} {}", activity.getType(), activity.getDescription());
  }

  @Override
  public PageResponse<ActivityLogDomain> findAll(ActivityLogCriteria criteria) {
    return activityLogRepository.findAll(criteria);
  }
}
