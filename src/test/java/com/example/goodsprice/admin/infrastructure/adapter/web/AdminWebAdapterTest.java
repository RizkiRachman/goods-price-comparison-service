package com.example.goodsprice.admin.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.admin.application.port.in.AdminInPort;
import com.example.goodsprice.api.model.AdminJobTriggerResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminWebAdapterTest {

  @Mock private AdminInPort adminInPort;

  @InjectMocks private AdminWebAdapter adapter;

  @Test
  @DisplayName("Should trigger job")
  void shouldTriggerJob() {
    var response = new AdminJobTriggerResponse();
    response.setJobName("testJob");
    response.setMessage("Job triggered successfully");

    when(adminInPort.triggerJob("testJob")).thenReturn(response);

    var result = adapter.trigger("testJob");

    assertNotNull(result);
    assertEquals("testJob", result.getJobName());
    assertEquals("Job triggered successfully", result.getMessage());
    verify(adminInPort).triggerJob("testJob");
  }
}
