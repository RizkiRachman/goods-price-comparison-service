package com.example.goodsprice.feedbackquestion.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.CreateFeedbackQuestionRequest;
import com.example.goodsprice.api.model.FeedbackQuestion;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionType;
import com.example.goodsprice.feedbackquestion.application.port.in.FeedbackQuestionInPort;
import com.example.goodsprice.feedbackquestion.application.port.in.dto.FeedbackQuestionCriteria;
import com.example.goodsprice.feedbackquestion.infrastructure.adapter.web.mapper.FeedbackQuestionDtoMapper;
import java.time.OffsetDateTime;
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
class FeedbackQuestionWebAdapterTest {

  @Mock private FeedbackQuestionInPort feedbackQuestionInPort;
  @Mock private FeedbackQuestionDtoMapper mapper;

  @InjectMocks private FeedbackQuestionWebAdapter adapter;

  @Captor private ArgumentCaptor<FeedbackQuestionCriteria> criteriaCaptor;

  private UUID id;
  private FeedbackQuestionDomain domain;
  private FeedbackQuestion apiModel;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    domain =
        FeedbackQuestionDomain.builder()
            .id(id)
            .userName("John")
            .userEmail("john@test.com")
            .type(FeedbackQuestionType.FEEDBACK)
            .message("Great!")
            .createdAt(OffsetDateTime.now())
            .build();
    apiModel = new FeedbackQuestion();
    apiModel.setId(id);
    apiModel.setUserName("John");
  }

  @Test
  @DisplayName("Should create feedback question with FEEDBACK type")
  void shouldCreateWithFeedbackType() {
    var request = new CreateFeedbackQuestionRequest();
    request.setUserName("John");
    request.setUserEmail("john@test.com");
    request.setType(CreateFeedbackQuestionRequest.TypeEnum.FEEDBACK);
    request.setMessage("Great!");

    when(feedbackQuestionInPort.create(
            eq("John"), eq("john@test.com"), eq(FeedbackQuestionType.FEEDBACK), eq("Great!")))
        .thenReturn(domain);
    when(mapper.toApiFeedbackQuestion(domain)).thenReturn(apiModel);

    var result = adapter.create(request);

    assertNotNull(result);
    assertEquals("John", result.getUserName());
    verify(feedbackQuestionInPort)
        .create("John", "john@test.com", FeedbackQuestionType.FEEDBACK, "Great!");
    verify(mapper).toApiFeedbackQuestion(domain);
  }

  @Test
  @DisplayName("Should default to QUESTION type when type is null")
  void shouldDefaultToQuestionType() {
    var request = new CreateFeedbackQuestionRequest();
    request.setUserName("Jane");
    request.setUserEmail("jane@test.com");
    request.setType(null);
    request.setMessage("How do I?");

    when(feedbackQuestionInPort.create(
            eq("Jane"), eq("jane@test.com"), eq(FeedbackQuestionType.QUESTION), eq("How do I?")))
        .thenReturn(domain);
    when(mapper.toApiFeedbackQuestion(domain)).thenReturn(apiModel);

    var result = adapter.create(request);

    assertNotNull(result);
    verify(feedbackQuestionInPort)
        .create("Jane", "jane@test.com", FeedbackQuestionType.QUESTION, "How do I?");
  }

  @Test
  @DisplayName("Should find by id")
  void shouldFindById() {
    when(feedbackQuestionInPort.findById(id)).thenReturn(domain);
    when(mapper.toApiFeedbackQuestion(domain)).thenReturn(apiModel);

    var result = adapter.findById(id);

    assertNotNull(result);
    assertEquals(id, result.getId());
    verify(feedbackQuestionInPort).findById(id);
    verify(mapper).toApiFeedbackQuestion(domain);
  }

  @Test
  @DisplayName("Should list feedback questions with pagination")
  void shouldList() {
    var pageResponse = PageResponse.of(List.of(domain), 1, 20, 1);

    when(feedbackQuestionInPort.findAll(any(FeedbackQuestionCriteria.class)))
        .thenReturn(pageResponse);
    when(mapper.toApiFeedbackQuestion(domain)).thenReturn(apiModel);

    var result = adapter.list(1, 20, "createdAt", "desc");

    assertNotNull(result);
    assertEquals(1, result.getData().size());
    verify(feedbackQuestionInPort).findAll(criteriaCaptor.capture());
    var criteria = criteriaCaptor.getValue();
    assertEquals(1, criteria.pageRequest().page());
    assertEquals(20, criteria.pageRequest().size());
  }

  @Test
  @DisplayName("Should list with default pagination")
  void shouldListWithDefaults() {
    var pageResponse = PageResponse.of(List.of(domain), 1, 20, 1);

    when(feedbackQuestionInPort.findAll(any(FeedbackQuestionCriteria.class)))
        .thenReturn(pageResponse);
    when(mapper.toApiFeedbackQuestion(domain)).thenReturn(apiModel);

    var result = adapter.list(null, null, null, null);

    assertNotNull(result);
    verify(feedbackQuestionInPort).findAll(criteriaCaptor.capture());
    var criteria = criteriaCaptor.getValue();
    assertEquals(1, criteria.pageRequest().page());
    assertEquals("createdAt", criteria.pageRequest().sortBy());
    assertEquals("desc", criteria.pageRequest().sortDirection());
  }
}
