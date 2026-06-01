package com.example.goodsprice.admin.application.domain.service;

import com.example.goodsprice.admin.application.port.in.AdminInPort;
import com.example.goodsprice.admin.job.JobRegistry;
import com.example.goodsprice.api.model.AdminJobTriggerResponse;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService implements AdminInPort {

  private final JobRegistry jobRegistry;

  @Override
  public AdminJobTriggerResponse triggerJob(String jobName) {
    var executor = jobRegistry.get(jobName);
    if (executor == null) {
      log.warn("Unknown job requested: {}", jobName);
      return new AdminJobTriggerResponse()
          .jobName(jobName)
          .message("Unknown job: " + jobName)
          .triggeredAt(OffsetDateTime.now(ZoneOffset.UTC));
    }
    log.info("Triggering job: {}", jobName);
    executor.run();
    log.info("Job completed: {}", jobName);
    return new AdminJobTriggerResponse()
        .jobName(jobName)
        .message("Job triggered successfully")
        .triggeredAt(OffsetDateTime.now(ZoneOffset.UTC));
  }
}
