package com.example.goodsprice.activity.infrastructure.adapter.web.mapper;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import com.example.goodsprice.api.model.ActivityLog;
import com.example.goodsprice.api.model.ActivityLog.ActionEnum;
import com.example.goodsprice.api.model.ActivityLog.TypeEnum;
import com.example.goodsprice.common.web.mapper.DtoMapperSupport;
import java.time.ZoneOffset;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogDtoMapper implements DtoMapperSupport {

  public ActivityLog toApiModel(ActivityLogDomain domain) {
    return mapIfNotNull(
        domain,
        d -> {
          var result = new ActivityLog();
          result.setId(d.getId());
          result.setType(toApiType(d.getType()));
          result.setAction(toApiAction(d.getAction()));
          result.setDescription(d.getDescription());
          if (Objects.nonNull(d.getCreatedAt())) {
            result.setCreatedAt(d.getCreatedAt().atOffset(ZoneOffset.UTC));
          }
          if (Objects.nonNull(d.getUpdatedAt())) {
            result.setUpdatedAt(d.getUpdatedAt().atOffset(ZoneOffset.UTC));
          }
          return result;
        });
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
