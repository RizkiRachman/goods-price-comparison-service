package com.example.goodsprice.activity.infrastructure.adapter.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:MethodName")
class ActivityLogDtoMapperTest {

  private final ActivityLogDtoMapper mapper = new ActivityLogDtoMapper();

  @Test
  void shouldMapToApiModel() {
    var domain =
        ActivityLogDomain.builder()
            .id(UUID.randomUUID())
            .type(ActivityLogType.STORE)
            .action(ActivityLogAction.CREATE)
            .description("Store created")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    var result = mapper.toApiModel(domain);
    assertThat(result).isNotNull();
    assertThat(result.getDescription()).isEqualTo("Store created");
    assertThat(result.getType())
        .isEqualTo(com.example.goodsprice.api.model.ActivityLog.TypeEnum.STORE);
    assertThat(result.getAction())
        .isEqualTo(com.example.goodsprice.api.model.ActivityLog.ActionEnum.CREATE);
  }

  @Test
  void shouldReturnNullForNullInput() {
    assertThat(mapper.toApiModel(null)).isNull();
  }
}
