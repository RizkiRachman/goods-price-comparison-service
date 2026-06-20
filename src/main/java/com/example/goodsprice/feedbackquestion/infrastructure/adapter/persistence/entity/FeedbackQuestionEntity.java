package com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence.entity;

import com.example.goodsprice.common.persistence.BaseTimestampEntity;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feedback_questions")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackQuestionEntity extends BaseTimestampEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private UUID id;

  @Column(name = "user_name", nullable = false, length = 100)
  private String userName;

  @Column(name = "user_email", nullable = false, length = 150)
  private String userEmail;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private FeedbackQuestionType type;

  @Column(name = "message", nullable = false, columnDefinition = "TEXT")
  private String message;
}
