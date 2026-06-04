package com.example.goodsprice.activity.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import com.example.goodsprice.activity.application.port.in.dto.ActivityLogCriteria;
import com.example.goodsprice.activity.infrastructure.adapter.persistence.entity.ActivityLogEntity;
import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ActivityLogRepositoryAdapterTest {

  @Mock private JpaActivityLogRepository jpaRepository;
  @Mock private ActivityLogMapper mapper;

  private ActivityLogRepositoryAdapter adapter;

  private UUID logId;
  private ActivityLogDomain domain;
  private ActivityLogEntity entity;

  @BeforeEach
  void setUp() {
    adapter = new ActivityLogRepositoryAdapter(jpaRepository, mapper);
    logId = UUID.randomUUID();
    domain =
        ActivityLogDomain.builder()
            .id(logId)
            .type(ActivityLogType.PRODUCT)
            .action(ActivityLogAction.CREATE)
            .description("Product created")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    entity = new ActivityLogEntity();
    entity.setId(logId);
    entity.setType(ActivityLogType.PRODUCT);
    entity.setAction(ActivityLogAction.CREATE);
    entity.setDescription("Product created");
  }

  @Test
  @DisplayName("Should save activity log")
  void shouldSave() {
    when(mapper.toEntity(domain)).thenReturn(entity);
    when(jpaRepository.save(entity)).thenReturn(entity);
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.save(domain);

    assertNotNull(result);
    assertEquals(logId, result.getId());
    verify(jpaRepository).save(entity);
  }

  @Test
  @DisplayName("Should find by id")
  void shouldFindById() {
    when(jpaRepository.findById(logId)).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.findById(logId);

    assertNotNull(result);
    assertEquals(logId, result.getId());
    verify(jpaRepository).findById(logId);
  }

  @Test
  @DisplayName("Should return null when id not found")
  void shouldReturnNullWhenNotFound() {
    when(jpaRepository.findById(logId)).thenReturn(Optional.empty());

    assertNull(adapter.findById(logId));
  }

  @Test
  @DisplayName("Should check exists by id")
  void shouldExistById() {
    when(jpaRepository.existsById(logId)).thenReturn(true);

    assertTrue(adapter.existsById(logId));
  }

  @Test
  @DisplayName("Should delete by id")
  void shouldDeleteById() {
    adapter.deleteById(logId);

    verify(jpaRepository).deleteById(logId);
  }

  @Test
  @DisplayName("Should find all with criteria with type filter")
  void shouldFindAllWithTypeFilter() {
    var pageRequest = new PageRequestDto(0, 20, "createdAt", "desc");
    var criteria = new ActivityLogCriteria(pageRequest, ActivityLogType.PRODUCT, null, null, null);

    var entityPage = new PageImpl<>(List.of(entity));
    when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(entityPage);
    when(mapper.toDomain(entity)).thenReturn(domain);

    PageResponse<ActivityLogDomain> result = adapter.findAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.content().size());
    assertEquals(logId, result.content().get(0).getId());
    verify(jpaRepository).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  @DisplayName("Should find all with criteria with action filter")
  void shouldFindAllWithActionFilter() {
    var pageRequest = new PageRequestDto(0, 20, "createdAt", "desc");
    var criteria = new ActivityLogCriteria(pageRequest, null, ActivityLogAction.CREATE, null, null);

    var entityPage = new PageImpl<>(List.of(entity));
    when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(entityPage);
    when(mapper.toDomain(entity)).thenReturn(domain);

    PageResponse<ActivityLogDomain> result = adapter.findAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.content().size());
  }

  @Test
  @DisplayName("Should find all with date range filter")
  void shouldFindAllWithDateRange() {
    var startDate = OffsetDateTime.now().minusDays(7);
    var endDate = OffsetDateTime.now();
    var pageRequest = new PageRequestDto(0, 20, "createdAt", "desc");
    var criteria = new ActivityLogCriteria(pageRequest, null, null, startDate, endDate);

    var entityPage = new PageImpl<>(List.of(entity));
    when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(entityPage);
    when(mapper.toDomain(entity)).thenReturn(domain);

    PageResponse<ActivityLogDomain> result = adapter.findAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.content().size());
  }

  @Test
  @DisplayName("Should find all without criteria filters")
  void shouldFindAllWithoutFilters() {
    var pageRequest = new PageRequestDto(0, 20, "createdAt", "desc");
    var criteria = new ActivityLogCriteria(pageRequest, null, null, null, null);

    var entityPage = new PageImpl<>(List.of(entity));
    when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(entityPage);
    when(mapper.toDomain(entity)).thenReturn(domain);

    PageResponse<ActivityLogDomain> result = adapter.findAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.content().size());
  }

  @Test
  @DisplayName("Should delegate to findAll with criteria for generic findAll")
  void shouldDelegateForGenericFindAll() {
    var pageRequest = new PageRequestDto(0, 20, "createdAt", "desc");
    var entityPage = new PageImpl<>(List.of(entity));
    when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(entityPage);
    when(mapper.toDomain(entity)).thenReturn(domain);

    PageResponse<ActivityLogDomain> result = adapter.findAll(pageRequest, null, null);

    assertNotNull(result);
    assertEquals(1, result.content().size());
  }

  @Test
  @DisplayName("Should return empty page when no results")
  void shouldReturnEmptyPage() {
    var pageRequest = new PageRequestDto(0, 20, "createdAt", "desc");
    var criteria = new ActivityLogCriteria(pageRequest, null, null, null, null);

    var emptyPage = new PageImpl<>(List.<ActivityLogEntity>of());
    when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(emptyPage);

    PageResponse<ActivityLogDomain> result = adapter.findAll(criteria);

    assertNotNull(result);
    assertTrue(result.content().isEmpty());
  }
}
