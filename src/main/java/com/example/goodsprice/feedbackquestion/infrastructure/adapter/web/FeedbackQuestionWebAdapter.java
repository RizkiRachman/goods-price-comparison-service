package com.example.goodsprice.feedbackquestion.infrastructure.adapter.web;

import com.example.goodsprice.api.model.CreateFeedbackQuestionRequest;
import com.example.goodsprice.api.model.FeedbackQuestion;
import com.example.goodsprice.api.model.FeedbackQuestionListResponse;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionType;
import com.example.goodsprice.feedbackquestion.application.port.in.FeedbackQuestionInPort;
import com.example.goodsprice.feedbackquestion.infrastructure.adapter.web.mapper.FeedbackQuestionDtoMapper;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackQuestionWebAdapter {

  private final FeedbackQuestionInPort feedbackQuestionInPort;
  private final FeedbackQuestionDtoMapper mapper;

  public FeedbackQuestion create(CreateFeedbackQuestionRequest request) {
    var type = FeedbackQuestionType.valueOf(request.getType().getValue().toUpperCase(Locale.ROOT));
    var domain =
        feedbackQuestionInPort.create(
            request.getUserName(), request.getUserEmail(), type, request.getMessage());
    return mapper.toApiFeedbackQuestion(domain);
  }

  public FeedbackQuestion findById(UUID id) {
    return mapper.toApiFeedbackQuestion(feedbackQuestionInPort.findById(id));
  }

  public FeedbackQuestionListResponse list(
      Integer page, Integer pageSize, String sortBy, String sortOrder) {
    var pageResponse =
        feedbackQuestionInPort.findAll(
            ObjectUtils.getOrDefault(page, p -> p, 1),
            ObjectUtils.getOrDefault(pageSize, s -> s, 20),
            ObjectUtils.getOrDefault(sortBy, s -> s, "createdAt"),
            ObjectUtils.getOrDefault(sortOrder, s -> s, "desc"));

    var response = new FeedbackQuestionListResponse();
    response.setData(pageResponse.content().stream().map(mapper::toApiFeedbackQuestion).toList());
    response.setPagination(pageResponse.toPagination());
    return response;
  }
}
