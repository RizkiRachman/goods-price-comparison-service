package com.example.goodsprice.feedbackquestion.infrastructure.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.goodsprice.api.model.CreateFeedbackQuestionRequest;
import com.example.goodsprice.api.model.FeedbackQuestion;
import com.example.goodsprice.api.model.FeedbackQuestionListResponse;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.common.web.AbstractControllerWebMvcTest;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class FeedbackQuestionControllerWebMvcTest extends AbstractControllerWebMvcTest {

  @Mock private FeedbackQuestionWebAdapter adapter;

  private final UUID questionId = UUID.randomUUID();

  @Override
  protected Object getController() {
    return new FeedbackQuestionController(adapter);
  }

  private FeedbackQuestion createApiFeedbackQuestion() {
    var question = new FeedbackQuestion();
    question.setId(questionId);
    question.setUserName("John Doe");
    question.setUserEmail("john@test.com");
    question.setType(FeedbackQuestion.TypeEnum.QUESTION);
    question.setMessage("How does price comparison work?");
    question.setCreatedAt(OffsetDateTime.parse("2026-01-01T00:00:00Z"));
    question.setUpdatedAt(OffsetDateTime.parse("2026-03-29T10:00:00Z"));
    return question;
  }

  @Test
  @DisplayName("POST /v1/feedback-questions should return 201 Created")
  void shouldCreateFeedbackQuestionReturns201() throws Exception {
    var question = createApiFeedbackQuestion();
    when(adapter.create(any(CreateFeedbackQuestionRequest.class))).thenReturn(question);

    var request = new CreateFeedbackQuestionRequest();
    request.setUserName("John Doe");
    request.setUserEmail("john@test.com");
    request.setType(CreateFeedbackQuestionRequest.TypeEnum.QUESTION);
    request.setMessage("How does price comparison work?");

    mockMvc
        .perform(
            post("/v1/feedback-questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(questionId.toString()))
        .andExpect(jsonPath("$.userName").value("John Doe"))
        .andExpect(jsonPath("$.type").value("question"));
  }

  @Test
  @DisplayName("GET /v1/feedback-questions/{id} should return 200 OK")
  void shouldGetFeedbackQuestionReturns200() throws Exception {
    var question = createApiFeedbackQuestion();
    when(adapter.findById(questionId)).thenReturn(question);

    mockMvc
        .perform(get("/v1/feedback-questions/{id}", questionId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(questionId.toString()))
        .andExpect(jsonPath("$.userName").value("John Doe"));
  }

  @Test
  @DisplayName("GET /v1/feedback-questions should return 200 OK with paginated list")
  void shouldListFeedbackQuestionsReturns200() throws Exception {
    var listResponse = new FeedbackQuestionListResponse();
    when(adapter.list(
            nullable(Integer.class),
            nullable(Integer.class),
            nullable(String.class),
            nullable(String.class)))
        .thenReturn(listResponse);

    mockMvc
        .perform(
            get("/v1/feedback-questions")
                .param("page", "1")
                .param("pageSize", "20")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("GET /v1/feedback-questions/{id} should return 404 when question not found")
  void shouldReturn404WhenFeedbackQuestionNotFound() throws Exception {
    when(adapter.findById(questionId))
        .thenThrow(
            new NotFoundException(
                "FEEDBACK_QUESTION_NOT_FOUND", "Feedback question not found: " + questionId));

    mockMvc
        .perform(get("/v1/feedback-questions/{id}", questionId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("FEEDBACK_QUESTION_NOT_FOUND"));
  }

  @Test
  @DisplayName("POST /v1/feedback-questions should return 400 when request body is invalid")
  void shouldReturn400WhenRequestBodyIsInvalid() throws Exception {
    mockMvc
        .perform(
            post("/v1/feedback-questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
        .andExpect(status().isBadRequest());
  }
}
