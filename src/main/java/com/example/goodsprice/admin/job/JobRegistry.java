package com.example.goodsprice.admin.job;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class JobRegistry {

  private final Map<String, JobExecutor> executors = new HashMap<>();

  public void register(String jobName, JobExecutor executor) {
    executors.put(jobName, executor);
  }

  public JobExecutor get(String jobName) {
    return executors.get(jobName);
  }

  public boolean contains(String jobName) {
    return Objects.nonNull(executors.get(jobName));
  }
}
