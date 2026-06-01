package com.example.goodsprice.admin.infrastructure.adapter.web;

import com.example.goodsprice.api.controller.AdminApi;
import com.example.goodsprice.api.model.AdminJobTriggerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminApi {

  private final AdminWebAdapter adapter;

  @Override
  public ResponseEntity<AdminJobTriggerResponse> triggerJob(String jobName) {
    return ResponseEntity.ok(adapter.trigger(jobName));
  }
}
