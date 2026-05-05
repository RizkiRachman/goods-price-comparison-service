package com.example.goodsprice.admin;

import com.example.goodsprice.admin.job.JobRegistry;
import com.example.goodsprice.api.model.AdminJobTriggerResponse;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminWebAdapter {

  private final JobRegistry jobRegistry;

  public AdminJobTriggerResponse trigger(String jobName) {
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
