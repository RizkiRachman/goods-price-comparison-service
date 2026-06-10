package com.example.goodsprice.common.web.mapper;

import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.common.util.ObjectUtils;
import java.util.Objects;
import java.util.function.Function;

/**
 * Shared support interface for DTO mappers. Provides null-guard wrapping and EntityStatus
 * resolution to eliminate boilerplate across all *DtoMapper implementations.
 */
public interface DtoMapperSupport {

  /**
   * Wraps a mapping function with a null guard. Returns null if domain is null.
   *
   * @param domain the domain object to map (may be null)
   * @param mapper the mapping function
   * @param <D> domain type
   * @param <R> result type
   * @return mapped result, or null if domain was null
   */
  default <D, R> R mapIfNotNull(D domain, Function<D, R> mapper) {
    if (Objects.isNull(domain)) return null;
    return mapper.apply(domain);
  }

  /**
   * Resolves a raw status string to an EntityStatus enum value, or null.
   *
   * @param statusValue raw status string from domain
   * @return EntityStatus enum value, or null
   */
  default EntityStatus resolveStatusValue(String statusValue) {
    return ObjectUtils.getOrNull(statusValue, EntityStatus::fromValue);
  }
}
