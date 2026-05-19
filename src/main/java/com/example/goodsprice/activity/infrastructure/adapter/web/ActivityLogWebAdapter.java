package com.example.goodsprice.activity.infrastructure.adapter.web;

import com.example.goodsprice.activity.application.port.in.ActivityLogInPort;
import com.example.goodsprice.activity.infrastructure.adapter.web.mapper.ActivityLogDtoMapper;
import com.example.goodsprice.api.model.ActivityLogListResponse;
import com.example.goodsprice.common.constant.AppConstants;
import com.example.goodsprice.common.util.ObjectUtils;
import java.time.OffsetDateTime;
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
    var pageValue = ObjectUtils.getOrDefault(page, p -> p, 1);
    var sizeValue = ObjectUtils.getOrDefault(pageSize, s -> s, AppConstants.DEFAULT_PAGE_SIZE);

    var pageResponse =
        activityLogInPort.findAll(
            pageValue, sizeValue, sortBy, sortOrder, type, action, startDate, endDate);

    var response = new ActivityLogListResponse();
    response.setData(pageResponse.content().stream().map(mapper::toApiModel).toList());
    response.setPagination(pageResponse.toPagination());
    return response;
  }
}
