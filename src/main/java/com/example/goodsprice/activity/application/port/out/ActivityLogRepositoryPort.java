package com.example.goodsprice.activity.application.port.out;

import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.common.dto.PageRequest;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import java.time.LocalDateTime;
import java.util.UUID;

public interface ActivityLogRepositoryPort extends GenericRepositoryPort<ActivityLogDomain, UUID> {

  PageResponse<ActivityLogDomain> findAll(
      PageRequest pageRequest,
      String type,
      String action,
      LocalDateTime startDate,
      LocalDateTime endDate);
}
