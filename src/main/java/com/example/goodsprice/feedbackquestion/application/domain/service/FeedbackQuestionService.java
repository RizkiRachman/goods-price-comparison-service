package com.example.goodsprice.feedbackquestion.application.domain.service;

import com.example.goodsprice.activity.application.annotation.ActivityLog;
import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.service.AbstractGenericService;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionType;
import com.example.goodsprice.feedbackquestion.application.port.in.FeedbackQuestionInPort;
import com.example.goodsprice.feedbackquestion.application.port.in.dto.FeedbackQuestionCriteria;
import com.example.goodsprice.feedbackquestion.application.port.out.FeedbackQuestionRepositoryPort;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class FeedbackQuestionService extends AbstractGenericService<FeedbackQuestionDomain, UUID>
    implements FeedbackQuestionInPort {

  private final FeedbackQuestionRepositoryPort feedbackQuestionRepository;

  public FeedbackQuestionService(FeedbackQuestionRepositoryPort feedbackQuestionRepository) {
    super("FeedbackQuestion", ErrorCodes.FEEDBACK_QUESTION_NOT_FOUND);
    this.feedbackQuestionRepository = feedbackQuestionRepository;
  }

  @Override
  protected FeedbackQuestionRepositoryPort getRepository() {
    return feedbackQuestionRepository;
  }

  @Override
  @Transactional
  @ActivityLog
  public FeedbackQuestionDomain create(
      String userName, String userEmail, FeedbackQuestionType type, String message) {
    var domain =
        FeedbackQuestionDomain.builder()
            .userName(userName)
            .userEmail(userEmail)
            .type(type)
            .message(message)
            .build();
    return save(domain);
  }

  @Override
  public PageResponse<FeedbackQuestionDomain> findAll(FeedbackQuestionCriteria criteria) {
    return findAll(criteria.pageRequest(), criteria.search(), criteria.status());
  }
}
