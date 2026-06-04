package com.example.goodsprice.admin.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.AdminJobTriggerResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

  @Mock private AdminWebAdapter adapter;

  @InjectMocks private AdminController controller;

  @Test
  @DisplayName("Should trigger job via controller")
  void shouldTriggerJob() {
    var response = new AdminJobTriggerResponse();
    response.setJobName("dataSync");
    response.setMessage("Job triggered successfully");

    when(adapter.trigger("dataSync")).thenReturn(response);

    var result = controller.triggerJob("dataSync");

    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals("dataSync", result.getBody().getJobName());
    assertEquals("Job triggered successfully", result.getBody().getMessage());
    verify(adapter).trigger("dataSync");
  }
}
