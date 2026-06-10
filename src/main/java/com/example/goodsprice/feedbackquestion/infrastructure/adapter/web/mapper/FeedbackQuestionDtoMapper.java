package com.example.goodsprice.feedbackquestion.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.FeedbackQuestion;
import com.example.goodsprice.common.web.mapper.DtoMapperSupport;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class FeedbackQuestionDtoMapper implements DtoMapperSupport {

  public FeedbackQuestion toApiFeedbackQuestion(FeedbackQuestionDomain domain) {
    return mapIfNotNull(
        domain,
        d -> {
          var result = new FeedbackQuestion();
          result.setId(d.getId());
          result.setUserName(d.getUserName());
          result.setUserEmail(d.getUserEmail());
          result.setType(
              Objects.nonNull(d.getType())
                  ? FeedbackQuestion.TypeEnum.fromValue(d.getType().name().toLowerCase(Locale.ROOT))
                  : null);
          result.setMessage(d.getMessage());
          result.setCreatedAt(d.getCreatedAt());
          result.setUpdatedAt(d.getUpdatedAt());
          return result;
        });
  }
}
