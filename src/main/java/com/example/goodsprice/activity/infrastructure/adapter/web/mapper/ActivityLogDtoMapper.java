package com.example.goodsprice.activity.infrastructure.adapter.web.mapper;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
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
    result.setType(toApiType(domain.getType()));
    result.setAction(toApiAction(domain.getAction()));
    result.setDescription(domain.getDescription());
    if (Objects.nonNull(domain.getCreatedAt())) {
      result.setCreatedAt(domain.getCreatedAt().atOffset(ZoneOffset.UTC));
    }
    if (Objects.nonNull(domain.getUpdatedAt())) {
      result.setUpdatedAt(domain.getUpdatedAt().atOffset(ZoneOffset.UTC));
    }
    return result;
  }

  private static TypeEnum toApiType(ActivityLogType type) {
    if (Objects.isNull(type)) return null;
    return TypeEnum.fromValue(type.name());
  }

  private static ActionEnum toApiAction(ActivityLogAction action) {
    if (Objects.isNull(action)) return null;
    return ActionEnum.fromValue(action.name());
  }
}
