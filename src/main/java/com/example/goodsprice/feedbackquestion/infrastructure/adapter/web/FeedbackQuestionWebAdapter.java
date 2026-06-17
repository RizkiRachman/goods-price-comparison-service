package com.example.goodsprice.feedbackquestion.infrastructure.adapter.web;

import com.example.goodsprice.api.model.CreateFeedbackQuestionRequest;
import com.example.goodsprice.api.model.FeedbackQuestion;
import com.example.goodsprice.api.model.FeedbackQuestionListResponse;
import com.example.goodsprice.common.web.AbstractCrudWebAdapter;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionType;
import com.example.goodsprice.feedbackquestion.application.port.in.FeedbackQuestionInPort;
import com.example.goodsprice.feedbackquestion.application.port.in.dto.FeedbackQuestionCriteria;
import com.example.goodsprice.feedbackquestion.infrastructure.adapter.web.mapper.FeedbackQuestionDtoMapper;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackQuestionWebAdapter extends AbstractCrudWebAdapter {

  private final FeedbackQuestionInPort feedbackQuestionInPort;
  private final FeedbackQuestionDtoMapper mapper;

  public FeedbackQuestion create(CreateFeedbackQuestionRequest request) {
    FeedbackQuestionType type;
    if (Objects.isNull(request.getType())) {
      type = FeedbackQuestionType.QUESTION;
    } else {
      type = FeedbackQuestionType.valueOf(request.getType().getValue().toUpperCase(Locale.ROOT));
    }
    var domain =
        FeedbackQuestionDomain.builder()
            .userName(request.getUserName())
            .userEmail(request.getUserEmail())
            .type(type)
            .message(request.getMessage())
            .build();
    return mapper.toApiFeedbackQuestion(feedbackQuestionInPort.create(domain));
  }

  public FeedbackQuestion findById(UUID id) {
    return mapper.toApiFeedbackQuestion(feedbackQuestionInPort.findById(id));
  }

  public FeedbackQuestionListResponse list(
      Integer page, Integer pageSize, String sortBy, String sortOrder) {
    var params = resolvePagination(page, pageSize, sortBy, sortOrder, "createdAt", "desc");
    var pageRequest = buildPageRequest(params);
    var criteria = new FeedbackQuestionCriteria(pageRequest, null, null);
    var pageResponse = feedbackQuestionInPort.findAll(criteria);

    return buildTypedListResponse(
        pageResponse, mapper::toApiFeedbackQuestion, FeedbackQuestionListResponse::new);
  }
}
