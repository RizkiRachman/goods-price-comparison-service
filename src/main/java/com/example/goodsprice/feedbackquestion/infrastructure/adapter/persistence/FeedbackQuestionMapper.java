package com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence;

import com.example.goodsprice.common.persistence.EntityMapperConfig;
import com.example.goodsprice.feedbackquestion.application.domain.model.FeedbackQuestionDomain;
import com.example.goodsprice.feedbackquestion.infrastructure.adapter.persistence.entity.FeedbackQuestionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = EntityMapperConfig.class)
public interface FeedbackQuestionMapper {

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  FeedbackQuestionEntity toEntity(FeedbackQuestionDomain domain);

  FeedbackQuestionDomain toDomain(FeedbackQuestionEntity entity);
}
