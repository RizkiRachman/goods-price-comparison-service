package com.example.goodsprice.alert.infrastructure.adapter.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;
import com.example.goodsprice.api.model.AlertSubscriptionResponse.StatusEnum;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@SuppressWarnings("checkstyle:MethodName")
class AlertDtoMapperTest {

  private final AlertDtoMapper mapper = Mappers.getMapper(AlertDtoMapper.class);

  @Test
  void shouldMapToResponse() {
    var sub =
        AlertSubscription.builder()
            .id("sub-1")
            .productName("Apple")
            .currentPrice(15000.0)
            .targetPrice(12000.0)
            .status("ACTIVE")
            .build();

    var result = mapper.toResponse(sub, "Price dropped!");
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(StatusEnum.ACTIVE);
    assertThat(result.getMessage()).isEqualTo("Price dropped!");
  }

  @Test
  void shouldDefaultToActiveForNullStatus() {
    assertThat(AlertDtoMapper.mapStatus(null)).isEqualTo(StatusEnum.ACTIVE);
  }
}
