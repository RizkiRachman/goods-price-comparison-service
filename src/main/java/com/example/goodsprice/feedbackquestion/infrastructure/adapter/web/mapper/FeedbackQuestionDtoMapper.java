package com.example.goodsprice.feedbackquestion.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.FeedbackQuestion;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class FeedbackQuestionDtoMapper {

  public FeedbackQuestion toApiFeedbackQuestion(FeedbackQuestionDomain domain) {
    if (Objects.isNull(domain)) return null;
    var result = new FeedbackQuestion();
    result.setId(domain.getId());
    result.setUserName(domain.getUserName());
    result.setUserEmail(domain.getUserEmail());
    result.setType(
        Objects.nonNull(domain.getType())
            ? FeedbackQuestion.TypeEnum.fromValue(domain.getType().name())
            : null);
    result.setMessage(domain.getMessage());
    result.setCreatedAt(domain.getCreatedAt());
    result.setUpdatedAt(domain.getUpdatedAt());
    return result;
  }
}
