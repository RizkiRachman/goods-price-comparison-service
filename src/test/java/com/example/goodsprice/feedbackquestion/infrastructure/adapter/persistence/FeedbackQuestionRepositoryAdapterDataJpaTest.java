package com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionType;
import com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence.entity.FeedbackQuestionEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FeedbackQuestionRepositoryAdapterDataJpaTest {

  @Autowired private JpaFeedbackQuestionRepository repository;

  @PersistenceContext private EntityManager entityManager;

  @Test
  @DisplayName("Should persist and retrieve feedback question with all fields")
  void shouldPersistAndRetrieveFeedbackQuestion() {
    var entity = new FeedbackQuestionEntity();
    entity.setUserName("John Doe");
    entity.setUserEmail("john@test.com");
    entity.setType(FeedbackQuestionType.QUESTION);
    entity.setMessage("How does price comparison work?");
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findById(entity.getId());

    assertTrue(found.isPresent());
    var q = found.get();
    assertNotNull(q.getId());
    assertEquals("John Doe", q.getUserName());
    assertEquals("john@test.com", q.getUserEmail());
    assertEquals(FeedbackQuestionType.QUESTION, q.getType());
    assertEquals("How does price comparison work?", q.getMessage());
  }

  @Test
  @DisplayName("Should persist feedback question type enum values")
  void shouldPersistTypeEnum() {
    var question = new FeedbackQuestionEntity();
    question.setUserName("Alice");
    question.setUserEmail("alice@test.com");
    question.setType(FeedbackQuestionType.QUESTION);
    question.setMessage("A question");
    repository.saveAndFlush(question);

    var feedback = new FeedbackQuestionEntity();
    feedback.setUserName("Bob");
    feedback.setUserEmail("bob@test.com");
    feedback.setType(FeedbackQuestionType.FEEDBACK);
    feedback.setMessage("Great app!");
    repository.saveAndFlush(feedback);
    entityManager.clear();

    assertEquals(
        FeedbackQuestionType.QUESTION,
        repository.findById(question.getId()).orElseThrow().getType());
    assertEquals(
        FeedbackQuestionType.FEEDBACK,
        repository.findById(feedback.getId()).orElseThrow().getType());
  }

  @Test
  @DisplayName("Should persist timestamps")
  void shouldPersistTimestamps() {
    var entity = new FeedbackQuestionEntity();
    entity.setUserName("Charlie");
    entity.setUserEmail("charlie@test.com");
    entity.setType(FeedbackQuestionType.QUESTION);
    entity.setMessage("Timestamp test");
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findById(entity.getId()).orElseThrow();

    assertNotNull(found.getCreatedAt());
    assertNotNull(found.getUpdatedAt());
  }

  @Test
  @DisplayName("Should delete feedback question")
  void shouldDeleteFeedbackQuestion() {
    var entity = new FeedbackQuestionEntity();
    entity.setUserName("Delete");
    entity.setUserEmail("delete@test.com");
    entity.setType(FeedbackQuestionType.FEEDBACK);
    entity.setMessage("Delete me");
    repository.saveAndFlush(entity);
    entityManager.clear();

    repository.deleteById(entity.getId());
    entityManager.flush();

    assertTrue(repository.findById(entity.getId()).isEmpty());
  }

  @Test
  @DisplayName("Should return empty for non-existent id")
  void shouldReturnEmptyForNonExistentId() {
    Optional<FeedbackQuestionEntity> found = repository.findById(UUID.randomUUID());
    assertTrue(found.isEmpty());
  }
}
