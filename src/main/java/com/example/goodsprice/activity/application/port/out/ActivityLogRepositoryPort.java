package com.example.goodsprice.activity.application.port.out;

import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.port.in.dto.ActivityLogCriteria;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import java.util.UUID;

public interface ActivityLogRepositoryPort extends GenericRepositoryPort<ActivityLogDomain, UUID> {

  PageResponse<ActivityLogDomain> findAll(ActivityLogCriteria criteria);
}
