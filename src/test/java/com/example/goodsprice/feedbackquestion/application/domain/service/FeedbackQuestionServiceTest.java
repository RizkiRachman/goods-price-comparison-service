package com.example.goodsprice.feedbackquestion.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionType;
import com.example.goodsprice.feedbackquestion.application.port.out.FeedbackQuestionRepositoryPort;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedbackQuestionServiceTest {

  @Mock private FeedbackQuestionRepositoryPort feedbackQuestionRepository;

  @InjectMocks private FeedbackQuestionService feedbackQuestionService;

  @Captor private ArgumentCaptor<FeedbackQuestionDomain> questionCaptor;

  private FeedbackQuestionDomain question;
  private UUID questionId;

  @BeforeEach
  void setUp() {
    questionId = UUID.randomUUID();
    question =
        FeedbackQuestionDomain.builder()
            .id(questionId)
            .userName("John")
            .userEmail("john@test.com")
            .type(FeedbackQuestionType.QUESTION)
            .message("How does pricing work?")
            .build();
  }

  @Test
  @DisplayName("Should create a feedback question")
  void shouldCreateFeedbackQuestion() {
    when(feedbackQuestionRepository.save(any(FeedbackQuestionDomain.class))).thenReturn(question);

    var result =
        feedbackQuestionService.create(
            "John", "john@test.com", FeedbackQuestionType.QUESTION, "How does pricing work?");

    assertNotNull(result);
    assertNotNull(result.getId());
    assertEquals("John", result.getUserName());
    assertEquals("john@test.com", result.getUserEmail());
    assertEquals(FeedbackQuestionType.QUESTION, result.getType());
    assertEquals("How does pricing work?", result.getMessage());
    verify(feedbackQuestionRepository).save(questionCaptor.capture());
    var captured = questionCaptor.getValue();
    assertEquals("John", captured.getUserName());
    assertEquals(FeedbackQuestionType.QUESTION, captured.getType());
  }

  @Test
  @DisplayName("Should return all feedback questions with pagination")
  void shouldReturnAllFeedbackQuestions() {
    var pageResponse = PageResponse.of(List.of(question), 0, 10, 1);
    when(feedbackQuestionRepository.findAll(any(PageRequestDto.class), any(), any()))
        .thenReturn(pageResponse);

    var result = feedbackQuestionService.findAll(0, 10, "createdAt", "desc");

    assertNotNull(result);
    assertEquals(1, result.totalElements());
    assertEquals("John", result.content().get(0).getUserName());
    verify(feedbackQuestionRepository).findAll(any(PageRequestDto.class), any(), any());
  }

  @Test
  @DisplayName("Should throw NotFoundException when feedback question not found")
  void shouldThrowExceptionWhenFeedbackQuestionNotFound() {
    var unknownId = UUID.randomUUID();
    when(feedbackQuestionRepository.findById(unknownId)).thenReturn(null);

    var exception =
        assertThrows(NotFoundException.class, () -> feedbackQuestionService.findById(unknownId));
    assertEquals("FEEDBACK_QUESTION_NOT_FOUND", exception.getErrorCode());
  }
}
