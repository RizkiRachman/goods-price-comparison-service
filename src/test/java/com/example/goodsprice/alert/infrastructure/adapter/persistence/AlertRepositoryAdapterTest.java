package com.example.goodsprice.alert.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;
import com.example.goodsprice.alert.infrastructure.adapter.persistence.entity.AlertSubscriptionEntity;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlertRepositoryAdapterTest {

  @Mock private JpaAlertSubscriptionRepository jpaRepository;
  @Mock private AlertMapper mapper;

  private AlertRepositoryAdapter adapter;

  private AlertSubscription subscription;
  private AlertSubscriptionEntity entity;

  @BeforeEach
  void setUp() {
    adapter = new AlertRepositoryAdapter(jpaRepository, mapper);
    subscription =
        AlertSubscription.builder()
            .id("sub-1")
            .productId(1L)
            .productName("Apple")
            .targetPrice(12000.0)
            .currentPrice(15000.0)
            .notificationMethod("EMAIL")
            .email("user@test.com")
            .status("ACTIVE")
            .build();
    entity = new AlertSubscriptionEntity();
    entity.setId("sub-1");
    entity.setProductId(1L);
    entity.setProductName("Apple");
    entity.setTargetPrice(12000.0);
    entity.setCurrentPrice(15000.0);
    entity.setNotificationMethod("EMAIL");
    entity.setEmail("user@test.com");
    entity.setStatus("ACTIVE");
  }

  @Test
  @DisplayName("Should save subscription")
  void shouldSave() {
    when(mapper.toEntity(subscription)).thenReturn(entity);
    when(jpaRepository.save(entity)).thenReturn(entity);
    when(mapper.toDomain(entity)).thenReturn(subscription);

    var result = adapter.save(subscription);

    assertNotNull(result);
    assertEquals("sub-1", result.getId());
    verify(jpaRepository).save(entity);
  }

  @Test
  @DisplayName("Should find by id")
  void shouldFindById() {
    when(jpaRepository.findById("sub-1")).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(subscription);

    var result = adapter.findById("sub-1");

    assertNotNull(result);
    assertEquals("sub-1", result.getId());
    verify(jpaRepository).findById("sub-1");
  }

  @Test
  @DisplayName("Should return null when id not found")
  void shouldReturnNullWhenNotFound() {
    when(jpaRepository.findById("unknown")).thenReturn(Optional.empty());

    assertNull(adapter.findById("unknown"));
  }

  @Test
  @DisplayName("Should check exists by id")
  void shouldExistById() {
    when(jpaRepository.existsById("sub-1")).thenReturn(true);

    assertTrue(adapter.existsById("sub-1"));
  }

  @Test
  @DisplayName("Should delete by id")
  void shouldDeleteById() {
    adapter.deleteById("sub-1");

    verify(jpaRepository).deleteById("sub-1");
  }
}
