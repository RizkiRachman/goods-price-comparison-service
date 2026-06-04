package com.example.goodsprice.feedbackquestion.infrastructure.adapter.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.goodsprice.api.model.FeedbackQuestion;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionType;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:MethodName")
class FeedbackQuestionDtoMapperTest {

  private final FeedbackQuestionDtoMapper mapper = new FeedbackQuestionDtoMapper();

  @Test
  void shouldMapToApiFeedbackQuestion() {
    var domain =
        FeedbackQuestionDomain.builder()
            .id(UUID.randomUUID())
            .userName("John")
            .userEmail("john@test.com")
            .type(FeedbackQuestionType.FEEDBACK)
            .message("Great!")
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    var result = mapper.toApiFeedbackQuestion(domain);
    assertThat(result).isNotNull();
    assertThat(result.getUserName()).isEqualTo("John");
    assertThat(result.getType()).isEqualTo(FeedbackQuestion.TypeEnum.FEEDBACK);
  }

  @Test
  void shouldReturnNullForNullInput() {
    assertThat(mapper.toApiFeedbackQuestion(null)).isNull();
  }
}
