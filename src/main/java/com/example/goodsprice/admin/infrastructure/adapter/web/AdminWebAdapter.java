package com.example.goodsprice.admin.infrastructure.adapter.web;

import com.example.goodsprice.admin.application.port.in.AdminInPort;
import com.example.goodsprice.api.model.AdminJobTriggerResponse;
import com.example.goodsprice.common.util.LogSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminWebAdapter {

  private final AdminInPort adminInPort;

  public AdminJobTriggerResponse trigger(String jobName) {
    var sanitizedJobName = LogSanitizer.sanitize(jobName, 100);
    log.info("ADMIN_ACTION: job triggered, jobName={}", sanitizedJobName);
    var result = adminInPort.triggerJob(jobName);
    log.info("ADMIN_ACTION: job completed, jobName={}, result={}", sanitizedJobName, result);
    return result;
  }
}
