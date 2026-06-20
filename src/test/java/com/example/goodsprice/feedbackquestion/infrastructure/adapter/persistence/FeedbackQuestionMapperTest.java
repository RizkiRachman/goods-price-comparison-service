package com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionType;
import com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence.entity.FeedbackQuestionEntity;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@SuppressWarnings("checkstyle:MethodName")
class FeedbackQuestionMapperTest {

  private final FeedbackQuestionMapper mapper = Mappers.getMapper(FeedbackQuestionMapper.class);

  @Test
  void shouldMapDomainToEntity() {
    var domain =
        FeedbackQuestionDomain.builder()
            .userName("John")
            .userEmail("john@test.com")
            .type(FeedbackQuestionType.QUESTION)
            .message("How do I return?")
            .build();

    var entity = mapper.toEntity(domain);

    assertThat(entity).isNotNull();
    assertThat(entity.getUserName()).isEqualTo("John");
    assertThat(entity.getUserEmail()).isEqualTo("john@test.com");
    assertThat(entity.getType()).isEqualTo(FeedbackQuestionType.QUESTION);
    assertThat(entity.getMessage()).isEqualTo("How do I return?");
  }

  @Test
  void shouldReturnNullForNullDomain() {
    assertThat(mapper.toEntity(null)).isNull();
  }

  @Test
  void shouldMapEntityToDomain() {
    var id = UUID.randomUUID();
    var now = LocalDateTime.of(2026, 6, 20, 12, 0, 0);
    var entity = new FeedbackQuestionEntity();
    entity.setId(id);
    entity.setUserName("Jane");
    entity.setUserEmail("jane@test.com");
    entity.setType(FeedbackQuestionType.FEEDBACK);
    entity.setMessage("Great app!");
    entity.setCreatedAt(now);
    entity.setUpdatedAt(now);

    var domain = mapper.toDomain(entity);

    assertThat(domain).isNotNull();
    assertThat(domain.getId()).isEqualTo(id);
    assertThat(domain.getUserName()).isEqualTo("Jane");
    assertThat(domain.getUserEmail()).isEqualTo("jane@test.com");
    assertThat(domain.getType()).isEqualTo(FeedbackQuestionType.FEEDBACK);
    assertThat(domain.getMessage()).isEqualTo("Great app!");
    assertThat(domain.getCreatedAt()).isEqualTo(now.atOffset(ZoneOffset.UTC));
    assertThat(domain.getUpdatedAt()).isEqualTo(now.atOffset(ZoneOffset.UTC));
  }

  @Test
  void shouldReturnNullForNullEntity() {
    assertThat(mapper.toDomain(null)).isNull();
  }
}
