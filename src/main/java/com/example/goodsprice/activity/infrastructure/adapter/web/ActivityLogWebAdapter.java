package com.example.goodsprice.activity.infrastructure.adapter.web;

import static com.example.goodsprice.common.util.PaginationUtils.resolvePage;
import static com.example.goodsprice.common.util.PaginationUtils.resolveSize;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import com.example.goodsprice.activity.application.port.in.ActivityLogInPort;
import com.example.goodsprice.activity.infrastructure.adapter.web.mapper.ActivityLogDtoMapper;
import com.example.goodsprice.api.model.ActivityLogListResponse;
import com.example.goodsprice.common.constant.AppConstants;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityLogWebAdapter {

  private final ActivityLogInPort activityLogInPort;
  private final ActivityLogDtoMapper mapper;

  public com.example.goodsprice.api.model.ActivityLog getById(java.util.UUID id) {
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
    var pageValue = resolvePage(page, 1);
    var sizeValue = resolveSize(pageSize, AppConstants.DEFAULT_PAGE_SIZE);
    var pageRequest =
        new com.example.goodsprice.common.dto.PageRequestDto(
            pageValue, sizeValue, sortBy, sortOrder);

    var typeEnum = parseType(type);
    var actionEnum = parseAction(action);
    var criteria =
        new com.example.goodsprice.activity.application.port.in.dto.ActivityLogCriteria(
            pageRequest, typeEnum, actionEnum, startDate, endDate);
    var pageResponse = activityLogInPort.findAll(criteria);

    var response = new ActivityLogListResponse();
    response.setData(pageResponse.content().stream().map(mapper::toApiModel).toList());
    response.setPagination(pageResponse.toPagination());
    return response;
  }

  private static ActivityLogType parseType(String type) {
    if (Objects.isNull(type) || type.isBlank()) return null;
    try {
      return ActivityLogType.valueOf(type.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException e) {
      log.warn("Invalid activity log type filter: {}", sanitizeForLog(type));
      return null;
    }
  }

  private static ActivityLogAction parseAction(String action) {
    if (Objects.isNull(action) || action.isBlank()) return null;
    try {
      return ActivityLogAction.valueOf(action.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException e) {
      log.warn("Invalid activity log action filter: {}", sanitizeForLog(action));
      return null;
    }
  }

  private static String sanitizeForLog(String value) {
    if (value == null) return null;
    var normalized = value.replaceAll("[^A-Za-z0-9._-]", "_");
    return normalized.length() > 100 ? normalized.substring(0, 100) : normalized;
  }
}
