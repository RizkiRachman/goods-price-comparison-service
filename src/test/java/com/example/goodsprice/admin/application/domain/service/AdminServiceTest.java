package com.example.goodsprice.admin.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.admin.job.JobExecutor;
import com.example.goodsprice.admin.job.JobRegistry;
import com.example.goodsprice.api.model.AdminJobTriggerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

  private AdminService adminService;

  @Mock private JobRegistry jobRegistry;
  @Mock private JobExecutor jobExecutor; // Mock for the executor returned by JobRegistry

  @BeforeEach
  void setUp() {
    adminService = new AdminService(jobRegistry);
  }

  @Test
  @DisplayName("Should trigger job successfully when jobName is valid")
  void triggerJobSuccess() {
    String jobName = "testJob";
    when(jobRegistry.get(jobName)).thenReturn(jobExecutor);

    AdminJobTriggerResponse response = adminService.triggerJob(jobName);

    assertNotNull(response);
    assertEquals(jobName, response.getJobName());
    assertEquals("Job triggered successfully", response.getMessage());
    assertNotNull(response.getTriggeredAt());
    verify(jobRegistry, times(1)).get(jobName);
    verify(jobExecutor, times(1)).run();
  }

  @Test
  @DisplayName("Should return unknown job response when jobName is not found")
  void triggerJobUnknownJob() {
    String jobName = "unknownJob";
    when(jobRegistry.get(jobName)).thenReturn(null);

    AdminJobTriggerResponse response = adminService.triggerJob(jobName);

    assertNotNull(response);
    assertEquals(jobName, response.getJobName());
    assertTrue(response.getMessage().contains("Unknown job: " + jobName));
    assertNotNull(response.getTriggeredAt());
    verify(jobRegistry, times(1)).get(jobName);
    verify(jobExecutor, never()).run(); // Ensure run is not called for unknown job
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when jobName is null")
  void triggerJobNullJobName() {
    String jobName = null;

    assertThrows(IllegalArgumentException.class, () -> adminService.triggerJob(jobName));

    verify(jobRegistry, never()).get(anyString()); // Ensure get is not called
    verify(jobExecutor, never()).run(); // Ensure run is not called
  }
}
