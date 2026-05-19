package com.example.goodsprice.activity.infrastructure.adapter.web.mapper;

import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.api.model.ActivityLog;
import com.example.goodsprice.api.model.ActivityLog.ActionEnum;
import com.example.goodsprice.api.model.ActivityLog.TypeEnum;
import java.time.ZoneOffset;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogDtoMapper {

  public ActivityLog toApiModel(ActivityLogDomain domain) {
    if (Objects.isNull(domain)) return null;
    var result = new ActivityLog();
    result.setId(domain.getId());
    result.setType(TypeEnum.fromValue(domain.getType()));
    result.setAction(ActionEnum.fromValue(domain.getAction()));
    result.setDescription(domain.getDescription());
    if (Objects.nonNull(domain.getCreatedAt())) {
      result.setCreatedAt(domain.getCreatedAt().atOffset(ZoneOffset.UTC));
    }
    if (Objects.nonNull(domain.getUpdatedAt())) {
      result.setUpdatedAt(domain.getUpdatedAt().atOffset(ZoneOffset.UTC));
    }
    return result;
  }
}
