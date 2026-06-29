package com.example.goodsprice.feedbackquestion.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import com.example.goodsprice.common.service.AbstractGenericService;
import com.example.goodsprice.common.service.AbstractGenericServiceTest;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionType;
import com.example.goodsprice.feedbackquestion.application.port.in.dto.FeedbackQuestionCriteria;
import com.example.goodsprice.feedbackquestion.application.port.out.FeedbackQuestionRepositoryPort;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedbackQuestionServiceTest extends AbstractGenericServiceTest {

  @Mock private FeedbackQuestionRepositoryPort feedbackQuestionRepository;

  @InjectMocks private FeedbackQuestionService feedbackQuestionService;

  private FeedbackQuestionDomain existingQuestion;
  private UUID questionId;

  @Override
  protected AbstractGenericService getService() {
    return feedbackQuestionService;
  }

  @Override
  protected Object getExistingId() {
    return questionId;
  }

  @Override
  protected Object getNonExistentId() {
    return UUID.fromString("00000000-0000-0000-0000-000000000001");
  }

  @Override
  protected Object getExistingEntity() {
    return existingQuestion;
  }

  @Override
  protected String getNotFoundErrorCode() {
    return "FEEDBACK_QUESTION_NOT_FOUND";
  }

  @Override
  protected GenericRepositoryPort getRepository() {
    return feedbackQuestionRepository;
  }

  @BeforeEach
  void setUp() {
    questionId = UUID.randomUUID();
    existingQuestion =
        FeedbackQuestionDomain.builder()
            .id(questionId)
            .userName("userName")
            .userEmail("userEmail")
            .type(FeedbackQuestionType.FEEDBACK)
            .message("message")
            .build();
  }

  @Test
  @DisplayName("Should create a new feedback question successfully")
  void createSuccess() {
    var domain =
        FeedbackQuestionDomain.builder()
            .userName("userName")
            .userEmail("userEmail")
            .type(FeedbackQuestionType.FEEDBACK)
            .message("message")
            .build();
    var savedDomain = FeedbackQuestionDomain.builder().build();
    when(feedbackQuestionRepository.save(any(FeedbackQuestionDomain.class)))
        .thenReturn(savedDomain);

    FeedbackQuestionDomain createdFeedbackQuestion = feedbackQuestionService.create(domain);

    assertNotNull(createdFeedbackQuestion);
    verify(feedbackQuestionRepository).save(any(FeedbackQuestionDomain.class));
  }

  @Test
  @DisplayName("Should return a list of feedback questions when findAll is called")
  void findAllSuccess() {
    FeedbackQuestionCriteria criteria = new FeedbackQuestionCriteria(null, null, null);
    PageResponse<FeedbackQuestionDomain> pageResponse =
        new PageResponse<>(List.of(), 0, 0, 0, 0, true, true);
    when(feedbackQuestionRepository.findAll(any(FeedbackQuestionCriteria.class)))
        .thenReturn(pageResponse);

    PageResponse<FeedbackQuestionDomain> actualFeedbackQuestions =
        feedbackQuestionService.findAll(criteria);

    assertNotNull(actualFeedbackQuestions);
    verify(feedbackQuestionRepository).findAll(any(FeedbackQuestionCriteria.class));
  }
}
