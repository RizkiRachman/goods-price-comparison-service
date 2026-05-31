package com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.persistence.PaginationHelper;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import com.example.goodsprice.feedbackquestion.application.port.out.FeedbackQuestionRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackQuestionRepositoryAdapter implements FeedbackQuestionRepositoryPort {

  private final JpaFeedbackQuestionRepository jpaRepo;
  private final FeedbackQuestionMapper mapper;

  @Override
  @CachePut(value = "feedback-questions", key = "#result.id")
  public FeedbackQuestionDomain save(FeedbackQuestionDomain domain) {
    var entity = mapper.toEntity(domain);
    var saved = jpaRepo.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  @Cacheable("feedback-questions")
  public FeedbackQuestionDomain findById(UUID id) {
    return jpaRepo.findById(id).map(mapper::toDomain).orElse(null);
  }

  @Override
  public PageResponse<FeedbackQuestionDomain> findAll(
      PageRequestDto pageRequest, String search, String status) {
    return PaginationHelper.findAll(
        pageRequest, (root, query, cb) -> cb.isTrue(cb.literal(true)), jpaRepo, mapper::toDomain);
  }

  @Override
  public boolean existsById(UUID id) {
    return jpaRepo.existsById(id);
  }

  @Override
  public void deleteById(UUID id) {
    jpaRepo.deleteById(id);
  }
}
