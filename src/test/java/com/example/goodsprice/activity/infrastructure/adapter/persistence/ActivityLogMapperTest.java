package com.example.goodsprice.activity.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import com.example.goodsprice.activity.infrastructure.adapter.persistence.entity.ActivityLogEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:MethodName")
class ActivityLogMapperTest {

  private final ActivityLogMapper mapper = new ActivityLogMapper();

  @Test
  void shouldMapDomainToEntity() {
    var now = LocalDateTime.now();
    var domain =
        ActivityLogDomain.builder()
            .id(UUID.randomUUID())
            .type(ActivityLogType.PRODUCT)
            .action(ActivityLogAction.CREATE)
            .description("Product created")
            .createdAt(now)
            .updatedAt(now)
            .build();

    var entity = mapper.toEntity(domain);

    assertThat(entity).isNotNull();
    assertThat(entity.getType()).isEqualTo(ActivityLogType.PRODUCT);
    assertThat(entity.getAction()).isEqualTo(ActivityLogAction.CREATE);
    assertThat(entity.getDescription()).isEqualTo("Product created");
  }

  @Test
  void shouldReturnNullForNullDomain() {
    assertThat(mapper.toEntity(null)).isNull();
  }

  @Test
  void shouldMapEntityToDomain() {
    var id = UUID.randomUUID();
    var now = LocalDateTime.now();
    var entity = new ActivityLogEntity();
    entity.setId(id);
    entity.setType(ActivityLogType.STORE);
    entity.setAction(ActivityLogAction.UPDATE);
    entity.setDescription("Store updated");
    entity.setCreatedAt(now);
    entity.setUpdatedAt(now);

    var domain = mapper.toDomain(entity);

    assertThat(domain).isNotNull();
    assertThat(domain.getId()).isEqualTo(id);
    assertThat(domain.getType()).isEqualTo(ActivityLogType.STORE);
    assertThat(domain.getAction()).isEqualTo(ActivityLogAction.UPDATE);
    assertThat(domain.getDescription()).isEqualTo("Store updated");
    assertThat(domain.getCreatedAt()).isEqualTo(now);
    assertThat(domain.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  void shouldReturnNullForNullEntity() {
    assertThat(mapper.toDomain(null)).isNull();
  }
}
