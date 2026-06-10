package com.example.goodsprice.common.persistence;

import org.mapstruct.MapperConfig;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Shared MapStruct mapper configuration for all persistence mappers (Entity ↔ Domain).
 *
 * <p>Use via {@code @Mapper(config = EntityMapperConfig.class)} on each persistence mapper
 * interface to eliminate boilerplate annotation attributes.
 */
@MapperConfig(
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntityMapperConfig {}
