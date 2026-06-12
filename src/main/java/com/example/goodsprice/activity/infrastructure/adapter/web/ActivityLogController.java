package com.example.goodsprice.activity.infrastructure.adapter.web;

import com.example.goodsprice.api.controller.ActivityLogsApi;
import com.example.goodsprice.api.model.ActivityLog;
import com.example.goodsprice.api.model.ActivityLogListResponse;
import com.example.goodsprice.common.web.ControllerResponse;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ActivityLogController implements ActivityLogsApi {

  private final ActivityLogWebAdapter adapter;

  @Override
  public ResponseEntity<ActivityLog> getActivityLog(UUID id) {
    return ControllerResponse.ok(adapter.getById(id));
  }

  @Override
  public ResponseEntity<ActivityLogListResponse> listActivityLogs(
      Integer page,
      Integer pageSize,
      String type,
      String action,
      OffsetDateTime from,
      OffsetDateTime to,
      String sortBy,
      String sortOrder) {
    return ControllerResponse.ok(
        adapter.list(page, pageSize, sortBy, sortOrder, type, action, from, to));
  }
}
