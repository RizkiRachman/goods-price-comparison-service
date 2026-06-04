package com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.persistence.PaginationHelper;
import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import com.example.goodsprice.feedbackquestion.application.port.in.dto.FeedbackQuestionCriteria;
import com.example.goodsprice.feedbackquestion.application.port.out.FeedbackQuestionRepositoryPort;
import com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence.entity.FeedbackQuestionEntity;
import java.util.UUID;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class FeedbackQuestionRepositoryAdapter
    extends AbstractRepositoryAdapter<FeedbackQuestionDomain, UUID, FeedbackQuestionEntity>
    implements FeedbackQuestionRepositoryPort {

  private final JpaFeedbackQuestionRepository jpaRepo;
  private final FeedbackQuestionMapper mapper;

  public FeedbackQuestionRepositoryAdapter(
      JpaFeedbackQuestionRepository jpaRepo, FeedbackQuestionMapper mapper) {
    this.jpaRepo = jpaRepo;
    this.mapper = mapper;
  }

  @Override
  protected JpaRepository<FeedbackQuestionEntity, UUID> getJpaRepository() {
    return jpaRepo;
  }

  @Override
  protected FeedbackQuestionEntity toEntity(FeedbackQuestionDomain domain) {
    return mapper.toEntity(domain);
  }

  @Override
  protected FeedbackQuestionDomain toDomain(FeedbackQuestionEntity entity) {
    return mapper.toDomain(entity);
  }

  @Override
  @CachePut(value = "feedback-questions", key = "#result.id")
  public FeedbackQuestionDomain save(FeedbackQuestionDomain domain) {
    return super.save(domain);
  }

  @Override
  @Cacheable("feedback-questions")
  public FeedbackQuestionDomain findById(UUID id) {
    return super.findById(id);
  }

  @Override
  public PageResponse<FeedbackQuestionDomain> findAll(
      PageRequestDto pageRequest, String search, String status) {
    return PaginationHelper.findAll(
        pageRequest, (root, query, cb) -> cb.isTrue(cb.literal(true)), jpaRepo, mapper::toDomain);
  }

  @Override
  public PageResponse<FeedbackQuestionDomain> findAll(FeedbackQuestionCriteria criteria) {
    return findAll(criteria.pageRequest(), criteria.search(), criteria.status());
  }
}
