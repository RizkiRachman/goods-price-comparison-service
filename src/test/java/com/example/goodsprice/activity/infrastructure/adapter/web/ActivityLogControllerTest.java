package com.example.goodsprice.activity.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.ActivityLog;
import com.example.goodsprice.api.model.ActivityLogListResponse;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ActivityLogControllerTest {

  @Mock private ActivityLogWebAdapter adapter;

  @InjectMocks private ActivityLogController controller;

  private UUID logId;
  private ActivityLog apiModel;

  @BeforeEach
  void setUp() {
    logId = UUID.randomUUID();
    apiModel = new ActivityLog();
    apiModel.setId(logId);
  }

  @Test
  @DisplayName("Should get activity log by id")
  void shouldGetActivityLog() {
    when(adapter.getById(logId)).thenReturn(apiModel);

    var response = controller.getActivityLog(logId);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(logId, response.getBody().getId());
    verify(adapter).getById(logId);
  }

  @Test
  @DisplayName("Should list activity logs")
  void shouldListActivityLogs() {
    var listResponse = new ActivityLogListResponse();
    lenient()
        .when(
            adapter.list(
                eq(1),
                eq(20),
                eq("createdAt"),
                eq("desc"),
                eq("PRODUCT"),
                eq("CREATE"),
                any(OffsetDateTime.class),
                any(OffsetDateTime.class)))
        .thenReturn(listResponse);

    var response =
        controller.listActivityLogs(
            1,
            20,
            "PRODUCT",
            "CREATE",
            OffsetDateTime.now().minusDays(7),
            OffsetDateTime.now(),
            "createdAt",
            "desc");

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
