package com.example.goodsprice.feedbackquestion.infrastructure.adapter.web;

import com.example.goodsprice.api.controller.FeedbackQuestionsApi;
import com.example.goodsprice.api.model.CreateFeedbackQuestionRequest;
import com.example.goodsprice.api.model.FeedbackQuestion;
import com.example.goodsprice.api.model.FeedbackQuestionListResponse;
import com.example.goodsprice.common.web.AbstractCrudController;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FeedbackQuestionController extends AbstractCrudController
    implements FeedbackQuestionsApi {

  private final FeedbackQuestionWebAdapter adapter;

  @Override
  public ResponseEntity<FeedbackQuestion> createFeedbackQuestion(
      @Valid CreateFeedbackQuestionRequest request) {
    return created(adapter.create(request));
  }

  @Override
  public ResponseEntity<FeedbackQuestion> getFeedbackQuestion(UUID id) {
    return ok(adapter.findById(id));
  }

  @Override
  public ResponseEntity<FeedbackQuestionListResponse> listFeedbackQuestions(
      Integer page, Integer pageSize, String sortBy, String sortOrder) {
    return ok(adapter.list(page, pageSize, sortBy, sortOrder));
  }
}
