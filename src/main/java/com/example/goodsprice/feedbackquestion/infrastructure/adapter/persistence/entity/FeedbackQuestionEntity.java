package com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence.entity;

import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "feedback_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackQuestionEntity {

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

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;
}
