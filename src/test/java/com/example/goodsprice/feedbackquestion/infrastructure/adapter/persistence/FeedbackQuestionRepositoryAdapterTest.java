package com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionType;
import com.example.goodsprice.feedbackquestion.application.port.in.dto.FeedbackQuestionCriteria;
import com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence.entity.FeedbackQuestionEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class FeedbackQuestionRepositoryAdapterTest {

  @Mock private JpaFeedbackQuestionRepository jpaRepo;
  @Mock private FeedbackQuestionMapper mapper;

  private FeedbackQuestionRepositoryAdapter adapter;

  private UUID id;
  private FeedbackQuestionDomain domain;
  private FeedbackQuestionEntity entity;

  @BeforeEach
  void setUp() {
    adapter = new FeedbackQuestionRepositoryAdapter(jpaRepo, mapper);
    id = UUID.randomUUID();
    domain =
        FeedbackQuestionDomain.builder()
            .id(id)
            .userName("John")
            .userEmail("john@test.com")
            .type(FeedbackQuestionType.FEEDBACK)
            .message("Great service!")
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();
    entity = new FeedbackQuestionEntity();
    entity.setId(id);
    entity.setUserName("John");
    entity.setUserEmail("john@test.com");
    entity.setType(FeedbackQuestionType.FEEDBACK);
    entity.setMessage("Great service!");
  }

  @Test
  @DisplayName("Should save feedback question")
  void shouldSave() {
    when(mapper.toEntity(domain)).thenReturn(entity);
    when(jpaRepo.save(entity)).thenReturn(entity);
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.save(domain);

    assertNotNull(result);
    assertEquals(id, result.getId());
    verify(jpaRepo).save(entity);
  }

  @Test
  @DisplayName("Should find by id")
  void shouldFindById() {
    when(jpaRepo.findById(id)).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.findById(id);

    assertNotNull(result);
    assertEquals(id, result.getId());
    verify(jpaRepo).findById(id);
  }

  @Test
  @DisplayName("Should return null when id not found")
  void shouldReturnNullWhenNotFound() {
    when(jpaRepo.findById(id)).thenReturn(Optional.empty());

    assertNull(adapter.findById(id));
  }

  @Test
  @DisplayName("Should check exists by id")
  void shouldExistById() {
    when(jpaRepo.existsById(id)).thenReturn(true);

    assertTrue(adapter.existsById(id));
  }

  @Test
  @DisplayName("Should delete by id")
  void shouldDeleteById() {
    adapter.deleteById(id);

    verify(jpaRepo).deleteById(id);
  }

  @Test
  @DisplayName("Should find all with pagination")
  void shouldFindAll() {
    var pageRequest = new PageRequestDto(0, 20, "createdAt", "desc");
    var criteria = new FeedbackQuestionCriteria(pageRequest, null, null);
    var page = new PageImpl<>(List.of(entity));

    when(jpaRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
    when(mapper.toDomain(entity)).thenReturn(domain);

    PageResponse<FeedbackQuestionDomain> result = adapter.findAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.content().size());
    assertEquals(id, result.content().get(0).getId());
  }

  @Test
  @DisplayName("Should return empty page when no results")
  void shouldReturnEmptyPage() {
    var pageRequest = new PageRequestDto(0, 20, "createdAt", "desc");
    var criteria = new FeedbackQuestionCriteria(pageRequest, null, null);
    var emptyPage = new PageImpl<>(List.<FeedbackQuestionEntity>of());

    when(jpaRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

    PageResponse<FeedbackQuestionDomain> result = adapter.findAll(criteria);

    assertNotNull(result);
    assertTrue(result.content().isEmpty());
  }

  @Test
  @DisplayName("Should find all with search term and status")
  void shouldFindAllWithSearchAndStatus() {
    var pageRequest = new PageRequestDto(0, 20, "createdAt", "desc");
    var criteria = new FeedbackQuestionCriteria(pageRequest, "John", "FEEDBACK");
    var page = new PageImpl<>(List.of(entity));

    when(jpaRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
    when(mapper.toDomain(entity)).thenReturn(domain);

    PageResponse<FeedbackQuestionDomain> result = adapter.findAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.content().size());
    assertEquals(id, result.content().get(0).getId());
  }

  @Test
  @DisplayName("Should find all with search and status via generic method")
  void shouldFindAllWithSearchAndStatusGeneric() {
    var pageRequest = new PageRequestDto(0, 20, "createdAt", "desc");
    var page = new PageImpl<>(List.of(entity));

    when(jpaRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
    when(mapper.toDomain(entity)).thenReturn(domain);

    PageResponse<FeedbackQuestionDomain> result = adapter.findAll(pageRequest, "John", "FEEDBACK");

    assertNotNull(result);
    assertEquals(1, result.content().size());
    assertEquals(id, result.content().get(0).getId());
  }
}
