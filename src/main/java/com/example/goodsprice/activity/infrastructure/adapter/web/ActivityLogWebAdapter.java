package com.example.goodsprice.activity.infrastructure.adapter.web;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import com.example.goodsprice.activity.application.port.in.ActivityLogInPort;
import com.example.goodsprice.activity.application.port.in.dto.ActivityLogCriteria;
import com.example.goodsprice.activity.infrastructure.adapter.web.mapper.ActivityLogDtoMapper;
import com.example.goodsprice.api.model.ActivityLog;
import com.example.goodsprice.api.model.ActivityLogListResponse;
import com.example.goodsprice.common.util.EnumParser;
import com.example.goodsprice.common.util.LogSanitizer;
import com.example.goodsprice.common.web.AbstractCrudWebAdapter;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityLogWebAdapter extends AbstractCrudWebAdapter {

  private final ActivityLogInPort activityLogInPort;
  private final ActivityLogDtoMapper mapper;

  public ActivityLog getById(UUID id) {
    var domain = activityLogInPort.findById(id);
    return mapper.toApiModel(domain);
  }

  public ActivityLogListResponse list(
      Integer page,
      Integer pageSize,
      String sortBy,
      String sortOrder,
      String type,
      String action,
      OffsetDateTime startDate,
      OffsetDateTime endDate) {
    var params = resolvePagination(page, pageSize, sortBy, sortOrder, "createdAt", "desc");
    var pageRequest = buildPageRequest(params);

    var typeEnum = parseType(type);
    var actionEnum = parseAction(action);
    var criteria = new ActivityLogCriteria(pageRequest, typeEnum, actionEnum, startDate, endDate);
    var pageResponse = activityLogInPort.findAll(criteria);

    var dp = buildListResponse(pageResponse, mapper::toApiModel);
    var response = new ActivityLogListResponse();
    response.setData(dp.data());
    response.setPagination(dp.pagination());
    return response;
  }

  private static ActivityLogType parseType(String type) {
    return EnumParser.parse(
        type,
        ActivityLogType.class,
        "activity log type",
        msg -> log.warn("Invalid activity log type filter: {}", LogSanitizer.sanitize(msg, 100)));
  }

  private static ActivityLogAction parseAction(String action) {
    return EnumParser.parse(
        action,
        ActivityLogAction.class,
        "activity log action",
        msg -> log.warn("Invalid activity log action filter: {}", LogSanitizer.sanitize(msg, 100)));
  }
}
