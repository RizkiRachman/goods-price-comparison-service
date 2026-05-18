package com.example.goodsprice.feedbackquestion.application.port.in;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionType;
import java.util.UUID;

public interface FeedbackQuestionInPort {

  FeedbackQuestionDomain create(
      String userName, String userEmail, FeedbackQuestionType type, String message);

  FeedbackQuestionDomain findById(UUID id);

  PageResponse<FeedbackQuestionDomain> findAll(
      int page, int size, String sortBy, String sortDirection);
}
