package com.example.goodsprice.feedbackquestion.application.port.in;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import com.example.goodsprice.feedbackquestion.application.port.in.dto.FeedbackQuestionCriteria;
import java.util.UUID;

public interface FeedbackQuestionInPort {

  FeedbackQuestionDomain create(FeedbackQuestionDomain domain);

  FeedbackQuestionDomain findById(UUID id);

  PageResponse<FeedbackQuestionDomain> findAll(FeedbackQuestionCriteria criteria);
}
