package com.example.goodsprice.activity.application.port.in;

import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.port.in.dto.ActivityLogCriteria;
import com.example.goodsprice.common.dto.PageResponse;
import java.util.UUID;

public interface ActivityLogInPort {

  void log(ActivityLogDomain activity);

  ActivityLogDomain findById(UUID id);

  PageResponse<ActivityLogDomain> findAll(ActivityLogCriteria criteria);
}
