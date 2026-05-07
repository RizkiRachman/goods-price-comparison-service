package com.example.goodsprice.price.infrastructure.adapter.persistence;

import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaPriceRepository extends JpaRepository<PriceEntity, Long> {

  List<PriceEntity> findByProductId(Long productId);

  @Query(
      "SELECT p FROM PriceEntity p WHERE p.productId = :productId AND p.dateRecorded BETWEEN :startDate AND :endDate ORDER BY p.dateRecorded ASC")
  List<PriceEntity> findByProductIdAndDateRange(
      @Param("productId") Long productId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Query("SELECT p FROM PriceEntity p WHERE p.productId = :productId ORDER BY p.price ASC")
  List<PriceEntity> findCheapestByProductId(@Param("productId") Long productId);

  @Query(
      "SELECT p FROM PriceEntity p WHERE p.productId IN :productIds AND p.price = (SELECT MIN(p2.price) FROM PriceEntity p2 WHERE p2.productId = p.productId)")
  List<PriceEntity> findCheapestByProductIds(@Param("productIds") List<Long> productIds);

  /**
   * Finds distinct product IDs that have prices recorded for specific stores.
   *
   * @param storeIds the list of store IDs
   * @return list of distinct product IDs
   */
  @Query("SELECT DISTINCT p.productId FROM PriceEntity p WHERE p.storeId IN :storeIds")
  List<Long> findDistinctProductIdsByStoreIds(@Param("storeIds") List<Long> storeIds);
}
