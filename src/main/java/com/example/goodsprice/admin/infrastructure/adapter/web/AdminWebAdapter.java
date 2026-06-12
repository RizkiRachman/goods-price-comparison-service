package com.example.goodsprice.admin.infrastructure.adapter.web;

import com.example.goodsprice.admin.application.port.in.AdminInPort;
import com.example.goodsprice.api.model.AdminJobTriggerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminWebAdapter {

  private final AdminInPort adminInPort;

  public AdminJobTriggerResponse trigger(String jobName) {
    log.info("ADMIN_ACTION: job triggered, jobName={}", jobName);
    var result = adminInPort.triggerJob(jobName);
    log.info("ADMIN_ACTION: job completed, jobName={}, result={}", jobName, result);
    return result;
  }
}
