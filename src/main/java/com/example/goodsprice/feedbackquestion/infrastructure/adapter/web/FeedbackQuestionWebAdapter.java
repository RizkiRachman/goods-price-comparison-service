package com.example.goodsprice.feedbackquestion.infrastructure.adapter.web;

import static com.example.goodsprice.common.util.PaginationUtils.resolvePage;
import static com.example.goodsprice.common.util.PaginationUtils.resolveSize;
import static com.example.goodsprice.common.util.PaginationUtils.resolveSortBy;
import static com.example.goodsprice.common.util.PaginationUtils.resolveSortOrder;

import com.example.goodsprice.api.model.CreateFeedbackQuestionRequest;
import com.example.goodsprice.api.model.FeedbackQuestion;
import com.example.goodsprice.api.model.FeedbackQuestionListResponse;
import com.example.goodsprice.common.constant.AppConstants;
import com.example.goodsprice.common.dto.PageRequestDto;
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
public class FeedbackQuestionWebAdapter {

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
        feedbackQuestionInPort.create(
            request.getUserName(), request.getUserEmail(), type, request.getMessage());
    return mapper.toApiFeedbackQuestion(domain);
  }

  public FeedbackQuestion findById(UUID id) {
    return mapper.toApiFeedbackQuestion(feedbackQuestionInPort.findById(id));
  }

  public FeedbackQuestionListResponse list(
      Integer page, Integer pageSize, String sortBy, String sortOrder) {
    var pageRequest =
        new PageRequestDto(
            resolvePage(page, 1),
            resolveSize(pageSize, AppConstants.DEFAULT_PAGE_SIZE),
            resolveSortBy(sortBy, "createdAt"),
            resolveSortOrder(sortOrder, "desc"));
    var criteria = new FeedbackQuestionCriteria(pageRequest, null, null);
    var pageResponse = feedbackQuestionInPort.findAll(criteria);

    var response = new FeedbackQuestionListResponse();
    response.setData(pageResponse.content().stream().map(mapper::toApiFeedbackQuestion).toList());
    response.setPagination(pageResponse.toPagination());
    return response;
  }
}
