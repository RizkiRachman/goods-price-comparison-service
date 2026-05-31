package com.example.goodsprice.activity.application.port.out;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import java.time.LocalDateTime;
import java.util.UUID;

public interface ActivityLogRepositoryPort extends GenericRepositoryPort<ActivityLogDomain, UUID> {

  PageResponse<ActivityLogDomain> findAll(
      PageRequestDto pageRequest,
      ActivityLogType type,
      ActivityLogAction action,
      LocalDateTime startDate,
      LocalDateTime endDate);
}
