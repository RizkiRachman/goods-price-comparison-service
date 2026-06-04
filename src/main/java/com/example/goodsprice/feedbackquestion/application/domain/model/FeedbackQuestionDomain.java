package com.example.goodsprice.feedbackquestion.application.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackQuestionDomain {

  private UUID id;
  private String userName;
  private String userEmail;
  private FeedbackQuestionType type;
  private String message;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
}
