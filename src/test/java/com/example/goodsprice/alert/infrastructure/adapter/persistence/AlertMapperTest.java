package com.example.goodsprice.alert.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;
import com.example.goodsprice.alert.infrastructure.adapter.persistence.entity.AlertSubscriptionEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@SuppressWarnings("checkstyle:MethodName")
class AlertMapperTest {

  private final AlertMapper mapper = Mappers.getMapper(AlertMapper.class);

  @Test
  void shouldMapDomainToEntity() {
    var domain =
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

    var entity = mapper.toEntity(domain);

    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isEqualTo("sub-1");
    assertThat(entity.getProductId()).isEqualTo(1L);
    assertThat(entity.getProductName()).isEqualTo("Apple");
    assertThat(entity.getTargetPrice()).isEqualTo(12000.0);
    assertThat(entity.getCurrentPrice()).isEqualTo(15000.0);
    assertThat(entity.getNotificationMethod()).isEqualTo("EMAIL");
    assertThat(entity.getEmail()).isEqualTo("user@test.com");
    assertThat(entity.getStatus()).isEqualTo("ACTIVE");
  }

  @Test
  void shouldReturnNullForNullDomain() {
    assertThat(mapper.toEntity(null)).isNull();
  }

  @Test
  void shouldMapEntityToDomain() {
    var entity = new AlertSubscriptionEntity();
    entity.setId("sub-2");
    entity.setProductId(2L);
    entity.setProductName("Bread");
    entity.setTargetPrice(5000.0);
    entity.setCurrentPrice(6000.0);
    entity.setNotificationMethod("SMS");
    entity.setEmail("08123456789");
    entity.setStatus("PAUSED");

    var domain = mapper.toDomain(entity);

    assertThat(domain).isNotNull();
    assertThat(domain.getId()).isEqualTo("sub-2");
    assertThat(domain.getProductId()).isEqualTo(2L);
    assertThat(domain.getProductName()).isEqualTo("Bread");
    assertThat(domain.getTargetPrice()).isEqualTo(5000.0);
    assertThat(domain.getCurrentPrice()).isEqualTo(6000.0);
    assertThat(domain.getNotificationMethod()).isEqualTo("SMS");
    assertThat(domain.getEmail()).isEqualTo("08123456789");
    assertThat(domain.getStatus()).isEqualTo("PAUSED");
  }

  @Test
  void shouldReturnNullForNullEntity() {
    assertThat(mapper.toDomain(null)).isNull();
  }
}
