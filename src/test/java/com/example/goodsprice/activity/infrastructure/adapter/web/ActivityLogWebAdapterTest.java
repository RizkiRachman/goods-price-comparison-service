package com.example.goodsprice.activity.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import com.example.goodsprice.activity.application.port.in.ActivityLogInPort;
import com.example.goodsprice.activity.application.port.in.dto.ActivityLogCriteria;
import com.example.goodsprice.activity.infrastructure.adapter.web.mapper.ActivityLogDtoMapper;
import com.example.goodsprice.api.model.ActivityLog;
import com.example.goodsprice.common.dto.PageResponse;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityLogWebAdapterTest {

  @Mock private ActivityLogInPort activityLogInPort;
  @Mock private ActivityLogDtoMapper mapper;

  @InjectMocks private ActivityLogWebAdapter adapter;

  @Captor private ArgumentCaptor<ActivityLogCriteria> criteriaCaptor;

  private UUID logId;
  private ActivityLogDomain domain;
  private ActivityLog apiModel;

  @BeforeEach
  void setUp() {
    logId = UUID.randomUUID();
    domain =
        ActivityLogDomain.builder()
            .id(logId)
            .type(ActivityLogType.PRODUCT)
            .action(ActivityLogAction.CREATE)
            .description("Product created")
            .createdAt(LocalDateTime.now())
            .build();
    apiModel = new ActivityLog();
    apiModel.setId(logId);
    apiModel.setDescription("Product created");
  }

  @Test
  @DisplayName("Should get by id")
  void shouldGetById() {
    when(activityLogInPort.findById(logId)).thenReturn(domain);
    when(mapper.toApiModel(domain)).thenReturn(apiModel);

    var result = adapter.getById(logId);

    assertNotNull(result);
    assertEquals(logId, result.getId());
    verify(activityLogInPort).findById(logId);
    verify(mapper).toApiModel(domain);
  }

  @Test
  @DisplayName("Should list with all filters")
  void shouldListWithFilters() {
    var startDate = OffsetDateTime.now().minusDays(7);
    var endDate = OffsetDateTime.now();
    var pageResponse = PageResponse.of(List.of(domain), 1, 20, 1);

    when(activityLogInPort.findAll(any(ActivityLogCriteria.class))).thenReturn(pageResponse);
    when(mapper.toApiModel(domain)).thenReturn(apiModel);

    var result = adapter.list(1, 20, "createdAt", "desc", "PRODUCT", "CREATE", startDate, endDate);

    assertNotNull(result);
    assertEquals(1, result.getData().size());
    assertEquals(logId, result.getData().get(0).getId());
    verify(activityLogInPort).findAll(criteriaCaptor.capture());

    var criteria = criteriaCaptor.getValue();
    assertEquals(ActivityLogType.PRODUCT, criteria.type());
    assertEquals(ActivityLogAction.CREATE, criteria.action());
  }

  @Test
  @DisplayName("Should list with null type and action")
  void shouldListWithNullTypeAndAction() {
    var pageResponse = PageResponse.of(List.of(domain), 1, 20, 1);

    when(activityLogInPort.findAll(any(ActivityLogCriteria.class))).thenReturn(pageResponse);
    when(mapper.toApiModel(domain)).thenReturn(apiModel);

    var result = adapter.list(1, 20, "createdAt", "desc", null, null, null, null);

    assertNotNull(result);
    assertEquals(1, result.getData().size());
    verify(activityLogInPort).findAll(criteriaCaptor.capture());

    var criteria = criteriaCaptor.getValue();
    assertNull(criteria.type());
    assertNull(criteria.action());
  }

  @Test
  @DisplayName("Should handle invalid type gracefully")
  void shouldHandleInvalidType() {
    var pageResponse = PageResponse.of(List.of(domain), 1, 20, 1);

    when(activityLogInPort.findAll(any(ActivityLogCriteria.class))).thenReturn(pageResponse);
    when(mapper.toApiModel(domain)).thenReturn(apiModel);

    var result = adapter.list(1, 20, "createdAt", "desc", "INVALID_TYPE", null, null, null);

    assertNotNull(result);
    assertEquals(1, result.getData().size());
    verify(activityLogInPort).findAll(criteriaCaptor.capture());
    assertNull(criteriaCaptor.getValue().type());
  }

  @Test
  @DisplayName("Should use default pagination for null values")
  void shouldUseDefaultPagination() {
    var pageResponse = PageResponse.of(List.of(domain), 1, 20, 1);

    when(activityLogInPort.findAll(any(ActivityLogCriteria.class))).thenReturn(pageResponse);
    when(mapper.toApiModel(domain)).thenReturn(apiModel);

    var result = adapter.list(null, null, null, null, null, null, null, null);

    assertNotNull(result);
    verify(activityLogInPort).findAll(criteriaCaptor.capture());
    var pr = criteriaCaptor.getValue().pageRequest();
    assertEquals(1, pr.page());
    assertEquals(20, pr.size());
  }
}
