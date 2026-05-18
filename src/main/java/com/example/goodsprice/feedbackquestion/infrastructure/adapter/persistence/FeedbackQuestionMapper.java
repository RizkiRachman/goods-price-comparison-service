package com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence;

import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence.entity.FeedbackQuestionEntity;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class FeedbackQuestionMapper {

  public FeedbackQuestionEntity toEntity(FeedbackQuestionDomain domain) {
    if (Objects.isNull(domain)) return null;
    var entity = new FeedbackQuestionEntity();
    entity.setId(domain.getId());
    entity.setUserName(domain.getUserName());
    entity.setUserEmail(domain.getUserEmail());
    entity.setType(domain.getType());
    entity.setMessage(domain.getMessage());
    return entity;
  }

  public FeedbackQuestionDomain toDomain(FeedbackQuestionEntity entity) {
    if (Objects.isNull(entity)) return null;
    return FeedbackQuestionDomain.builder()
        .id(entity.getId())
        .userName(entity.getUserName())
        .userEmail(entity.getUserEmail())
        .type(entity.getType())
        .message(entity.getMessage())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();
  }
}
