package com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.persistence.PaginationHelper;
import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import com.example.goodsprice.config.CacheConfiguration;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import com.example.goodsprice.feedbackquestion.application.port.in.dto.FeedbackQuestionCriteria;
import com.example.goodsprice.feedbackquestion.application.port.out.FeedbackQuestionRepositoryPort;
import com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence.entity.FeedbackQuestionEntity;
import java.util.UUID;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class FeedbackQuestionRepositoryAdapter
    extends AbstractRepositoryAdapter<FeedbackQuestionDomain, UUID, FeedbackQuestionEntity>
    implements FeedbackQuestionRepositoryPort {

  private final FeedbackQuestionMapper mapper;

  public FeedbackQuestionRepositoryAdapter(
      JpaFeedbackQuestionRepository jpaRepository, FeedbackQuestionMapper mapper) {
    super(jpaRepository, mapper::toEntity, mapper::toDomain);
    this.mapper = mapper;
  }

  @Override
  @CachePut(value = CacheConfiguration.FEEDBACK_QUESTIONS_CACHE, key = "#result.id")
  public FeedbackQuestionDomain save(FeedbackQuestionDomain domain) {
    return super.save(domain);
  }

  @Override
  @Cacheable(CacheConfiguration.FEEDBACK_QUESTIONS_CACHE)
  public FeedbackQuestionDomain findById(UUID id) {
    return super.findById(id);
  }

  @Override
  public PageResponse<FeedbackQuestionDomain> findAll(
      PageRequestDto pageRequest, String search, String status) {
    return PaginationHelper.findAll(
        pageRequest,
        (root, query, cb) -> cb.isTrue(cb.literal(true)),
        jpaSpecificationExecutor(),
        mapper::toDomain);
  }

  @Override
  public PageResponse<FeedbackQuestionDomain> findAll(FeedbackQuestionCriteria criteria) {
    return findAll(criteria.pageRequest(), criteria.search(), criteria.status());
  }
}
