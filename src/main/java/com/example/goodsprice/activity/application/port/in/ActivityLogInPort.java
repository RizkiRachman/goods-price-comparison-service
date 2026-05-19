package com.example.goodsprice.activity.application.port.in;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import com.example.goodsprice.common.dto.PageResponse;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface ActivityLogInPort {

  void log(ActivityLogDomain activity);

  ActivityLogDomain findById(UUID id);

  PageResponse<ActivityLogDomain> findAll(
      int page,
      int size,
      String sortBy,
      String sortDirection,
      ActivityLogType type,
      ActivityLogAction action,
      OffsetDateTime startDate,
      OffsetDateTime endDate);
}
