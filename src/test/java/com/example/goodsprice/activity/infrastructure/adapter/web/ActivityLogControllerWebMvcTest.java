package com.example.goodsprice.activity.infrastructure.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.goodsprice.api.model.ActivityLog;
import com.example.goodsprice.api.model.ActivityLog.ActionEnum;
import com.example.goodsprice.api.model.ActivityLog.TypeEnum;
import com.example.goodsprice.api.model.ActivityLogListResponse;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.common.web.AbstractControllerWebMvcTest;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class ActivityLogControllerWebMvcTest extends AbstractControllerWebMvcTest {

  @Mock private ActivityLogWebAdapter adapter;

  private final UUID logId = UUID.randomUUID();

  @Override
  protected Object getController() {
    return new ActivityLogController(adapter);
  }

  private ActivityLog createApiActivityLog() {
    var log = new ActivityLog();
    log.setId(logId);
    log.setType(TypeEnum.PRODUCT);
    log.setAction(ActionEnum.CREATE);
    log.setDescription("Created product");
    log.setCreatedAt(OffsetDateTime.parse("2026-01-01T00:00:00Z"));
    log.setUpdatedAt(OffsetDateTime.parse("2026-03-29T10:00:00Z"));
    return log;
  }

  @Test
  @DisplayName("GET /v1/activity-logs/{id} should return 200 OK")
  void shouldGetActivityLogReturns200() throws Exception {
    var activityLog = createApiActivityLog();
    when(adapter.getById(logId)).thenReturn(activityLog);

    mockMvc
        .perform(get("/v1/activity-logs/{id}", logId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(logId.toString()))
        .andExpect(jsonPath("$.type").value("PRODUCT"))
        .andExpect(jsonPath("$.action").value("CREATE"));
  }

  @Test
  @DisplayName("GET /v1/activity-logs should return 200 OK with paginated list")
  void shouldListActivityLogsReturns200() throws Exception {
    var listResponse = new ActivityLogListResponse();
    when(adapter.list(
            nullable(Integer.class),
            nullable(Integer.class),
            nullable(String.class),
            nullable(String.class),
            nullable(String.class),
            nullable(String.class),
            nullable(OffsetDateTime.class),
            nullable(OffsetDateTime.class)))
        .thenReturn(listResponse);

    mockMvc
        .perform(
            get("/v1/activity-logs")
                .param("page", "1")
                .param("pageSize", "20")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("GET /v1/activity-logs should return 200 OK with filters")
  void shouldListActivityLogsWithFiltersReturns200() throws Exception {
    var listResponse = new ActivityLogListResponse();
    when(adapter.list(
            nullable(Integer.class),
            nullable(Integer.class),
            nullable(String.class),
            nullable(String.class),
            any(String.class),
            any(String.class),
            nullable(OffsetDateTime.class),
            nullable(OffsetDateTime.class)))
        .thenReturn(listResponse);

    mockMvc
        .perform(
            get("/v1/activity-logs")
                .param("page", "1")
                .param("pageSize", "20")
                .param("type", "PRODUCT")
                .param("action", "CREATE")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("GET /v1/activity-logs/{id} should return 404 when activity log not found")
  void shouldReturn404WhenActivityLogNotFound() throws Exception {
    when(adapter.getById(logId))
        .thenThrow(
            new NotFoundException("ACTIVITY_LOG_NOT_FOUND", "Activity log not found: " + logId));

    mockMvc
        .perform(get("/v1/activity-logs/{id}", logId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("ACTIVITY_LOG_NOT_FOUND"));
  }
}
