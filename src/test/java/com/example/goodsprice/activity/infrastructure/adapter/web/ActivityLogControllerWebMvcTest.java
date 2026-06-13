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
import com.example.goodsprice.common.web.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ActivityLogControllerWebMvcTest {

  @Mock private ActivityLogWebAdapter adapter;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private final UUID logId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    objectMapper =
        Jackson2ObjectMapperBuilder.json()
            .modules(new JsonNullableModule(), new JavaTimeModule())
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    var controller = new ActivityLogController(adapter);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
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
