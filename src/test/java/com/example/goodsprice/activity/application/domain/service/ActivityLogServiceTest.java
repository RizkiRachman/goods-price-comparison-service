package com.example.goodsprice.activity.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import com.example.goodsprice.activity.application.port.out.ActivityLogRepositoryPort;
import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.exception.NotFoundException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
class ActivityLogServiceTest {

  @Mock private ActivityLogRepositoryPort activityLogRepository;

  @InjectMocks private ActivityLogService activityLogService;

  @Captor private ArgumentCaptor<ActivityLogDomain> logCaptor;

  private ActivityLogDomain activityLog;
  private UUID logId;

  @BeforeEach
  void setUp() {
    logId = UUID.randomUUID();
    activityLog =
        ActivityLogDomain.builder()
            .id(logId)
            .type(ActivityLogType.PRODUCT)
            .action(ActivityLogAction.CREATE)
            .description("Product created")
            .createdAt(LocalDateTime.now())
            .build();
  }

  @Test
  @DisplayName("Should log an activity")
  void shouldLogActivity() {
    when(activityLogRepository.save(any(ActivityLogDomain.class))).thenReturn(activityLog);

    activityLogService.log(activityLog);

    verify(activityLogRepository).save(logCaptor.capture());
    var captured = logCaptor.getValue();
    assertEquals(ActivityLogType.PRODUCT, captured.getType());
    assertEquals(ActivityLogAction.CREATE, captured.getAction());
    assertEquals("Product created", captured.getDescription());
  }

  @Test
  @DisplayName("Should find all with date filter using converted LocalDateTime parameters")
  void shouldFindAllWithDateFilter() {
    var startDate = OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    var endDate = OffsetDateTime.of(2026, 6, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    var expectedStart = startDate.toLocalDateTime();
    var expectedEnd = endDate.toLocalDateTime();

    var pageResponse = PageResponse.of(List.of(activityLog), 0, 20, 1);
    when(activityLogRepository.findAll(
            any(PageRequestDto.class),
            eq(ActivityLogType.PRODUCT),
            eq(ActivityLogAction.CREATE),
            eq(expectedStart),
            eq(expectedEnd)))
        .thenReturn(pageResponse);

    var result =
        activityLogService.findAll(
            0,
            20,
            "createdAt",
            "desc",
            ActivityLogType.PRODUCT,
            ActivityLogAction.CREATE,
            startDate,
            endDate);

    assertNotNull(result);
    assertEquals(1, result.totalElements());
    assertEquals(logId, result.content().get(0).getId());
    verify(activityLogRepository)
        .findAll(
            any(PageRequestDto.class),
            eq(ActivityLogType.PRODUCT),
            eq(ActivityLogAction.CREATE),
            eq(expectedStart),
            eq(expectedEnd));
  }

  @Test
  @DisplayName("Should handle null start and end dates")
  void shouldHandleNullDates() {
    var pageResponse = PageResponse.of(List.of(activityLog), 0, 20, 1);
    when(activityLogRepository.findAll(
            any(PageRequestDto.class),
            eq(ActivityLogType.PRODUCT),
            eq(ActivityLogAction.CREATE),
            eq(null),
            eq(null)))
        .thenReturn(pageResponse);

    var result =
        activityLogService.findAll(
            0,
            20,
            "createdAt",
            "desc",
            ActivityLogType.PRODUCT,
            ActivityLogAction.CREATE,
            null,
            null);

    assertNotNull(result);
    assertEquals(1, result.totalElements());
    verify(activityLogRepository)
        .findAll(
            any(PageRequestDto.class),
            eq(ActivityLogType.PRODUCT),
            eq(ActivityLogAction.CREATE),
            eq(null),
            eq(null));
  }

  @Test
  @DisplayName("Should find all with only start date and null end date")
  void shouldFindAllWithOnlyStartDate() {
    var startDate = OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    var expectedStart = startDate.toLocalDateTime();

    var pageResponse = PageResponse.of(List.of(activityLog), 0, 20, 1);
    when(activityLogRepository.findAll(
            any(PageRequestDto.class), eq(null), eq(null), eq(expectedStart), eq(null)))
        .thenReturn(pageResponse);

    var result =
        activityLogService.findAll(0, 20, "createdAt", "desc", null, null, startDate, null);

    assertNotNull(result);
    assertEquals(1, result.totalElements());
    verify(activityLogRepository)
        .findAll(any(PageRequestDto.class), eq(null), eq(null), eq(expectedStart), eq(null));
  }

  @Test
  @DisplayName("Should throw NotFoundException when activity log not found")
  void shouldThrowExceptionWhenActivityLogNotFound() {
    var unknownId = UUID.randomUUID();
    when(activityLogRepository.findById(unknownId)).thenReturn(null);

    var exception =
        assertThrows(NotFoundException.class, () -> activityLogService.findById(unknownId));
    assertEquals("ACTIVITY_LOG_NOT_FOUND", exception.getErrorCode());
  }
}
