package com.example.goodsprice.price.infrastructure.adapter.persistence;

import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceSummaryEntity;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Spring Data JPA repository for price summary entities. */
@Repository
public interface JpaPriceSummaryRepository extends JpaRepository<PriceSummaryEntity, Long> {

  /**
   * Finds price summaries for multiple product IDs.
   *
   * @param productIds the set of product IDs
   * @return list of price summary entities
   */
  List<PriceSummaryEntity> findByProductIdIn(Set<Long> productIds);
}
