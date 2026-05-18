package com.example.goodsprice.feedbackquestion.application.port.out;

import com.example.goodsprice.common.repository.GenericRepositoryPort;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import java.util.UUID;

public interface FeedbackQuestionRepositoryPort
    extends GenericRepositoryPort<FeedbackQuestionDomain, UUID> {}
