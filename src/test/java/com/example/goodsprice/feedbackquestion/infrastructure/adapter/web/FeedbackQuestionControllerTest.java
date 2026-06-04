package com.example.goodsprice.feedbackquestion.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.CreateFeedbackQuestionRequest;
import com.example.goodsprice.api.model.FeedbackQuestion;
import com.example.goodsprice.api.model.FeedbackQuestionListResponse;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class FeedbackQuestionControllerTest {

  @Mock private FeedbackQuestionWebAdapter adapter;

  @InjectMocks private FeedbackQuestionController controller;

  private UUID id;
  private FeedbackQuestion apiModel;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    apiModel = new FeedbackQuestion();
    apiModel.setId(id);
  }

  @Test
  @DisplayName("Should create feedback question")
  void shouldCreate() {
    var request = new CreateFeedbackQuestionRequest();
    request.setUserName("John");
    request.setUserEmail("john@test.com");
    request.setMessage("Great!");

    when(adapter.create(any(CreateFeedbackQuestionRequest.class))).thenReturn(apiModel);

    var response = controller.createFeedbackQuestion(request);

    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(id, response.getBody().getId());
    verify(adapter).create(request);
  }

  @Test
  @DisplayName("Should get feedback question by id")
  void shouldGetById() {
    when(adapter.findById(id)).thenReturn(apiModel);

    var response = controller.getFeedbackQuestion(id);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(id, response.getBody().getId());
    verify(adapter).findById(id);
  }

  @Test
  @DisplayName("Should list feedback questions")
  void shouldList() {
    var listResponse = new FeedbackQuestionListResponse();
    when(adapter.list(1, 20, "createdAt", "desc")).thenReturn(listResponse);

    var response = controller.listFeedbackQuestions(1, 20, "createdAt", "desc");

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(adapter).list(1, 20, "createdAt", "desc");
  }
}
