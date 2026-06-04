package com.example.goodsprice.feedbackquestion.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionType;
import com.example.goodsprice.feedbackquestion.application.port.in.dto.FeedbackQuestionCriteria;
import com.example.goodsprice.feedbackquestion.application.port.out.FeedbackQuestionRepositoryPort;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedbackQuestionServiceTest {

  @Mock private FeedbackQuestionRepositoryPort feedbackQuestionRepository;

  @InjectMocks private FeedbackQuestionService feedbackQuestionService;

  @Test
  @DisplayName("Should create a new feedback question successfully")
  void createSuccess() {
    // Given
    FeedbackQuestionDomain feedbackQuestion = FeedbackQuestionDomain.builder().build();
    when(feedbackQuestionRepository.save(any(FeedbackQuestionDomain.class)))
        .thenReturn(feedbackQuestion);

    // When
    FeedbackQuestionDomain createdFeedbackQuestion =
        feedbackQuestionService.create(
            "userName", "userEmail", FeedbackQuestionType.FEEDBACK, "message");

    // Then
    assertNotNull(createdFeedbackQuestion);
    verify(feedbackQuestionRepository).save(any(FeedbackQuestionDomain.class));
  }

  @Test
  @DisplayName("Should return a list of feedback questions when findAll is called")
  void findAllSuccess() {
    // Given
    FeedbackQuestionCriteria criteria = new FeedbackQuestionCriteria(null, null, null);
    PageResponse<FeedbackQuestionDomain> pageResponse =
        new PageResponse<>(Collections.emptyList(), 0, 0, 0, 0, true, true);
    when(feedbackQuestionRepository.findAll(any(), any(), any())).thenReturn(pageResponse);

    // When
    PageResponse<FeedbackQuestionDomain> actualFeedbackQuestions =
        feedbackQuestionService.findAll(criteria);

    // Then
    assertNotNull(actualFeedbackQuestions);
    verify(feedbackQuestionRepository).findAll(any(), any(), any());
  }
}
