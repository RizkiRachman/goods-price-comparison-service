package com.example.goodsprice.product.infrastructure.adapter.persistence;

import com.example.goodsprice.product.infrastructure.adapter.persistence.entity.ProductEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {

  Optional<ProductEntity> findByName(String name);

  boolean existsByName(String name);

  List<ProductEntity> findByCategory(String category);

  /**
   * Finds products that need their price summary recalculated. Returns products where
   * lastPriceUpdate > summaryLastCalculated or summaryLastCalculated is null.
   *
   * @param limit maximum number of products to return
   * @return list of products needing summary update
   */
  @Query(
      value =
          "SELECT p FROM ProductEntity p WHERE p.lastPriceUpdate > p.summaryLastCalculated OR p.summaryLastCalculated IS NULL ORDER BY p.lastPriceUpdate ASC")
  List<ProductEntity> findProductsNeedingSummaryUpdate(@Param("limit") int limit);

  /**
   * Updates the summaryLastCalculated timestamp for a product.
   *
   * @param productId the product ID
   * @param timestamp the timestamp to set
   */
  @Modifying
  @Query("UPDATE ProductEntity p SET p.summaryLastCalculated = :timestamp WHERE p.id = :productId")
  void updateSummaryLastCalculated(
      @Param("productId") Long productId, @Param("timestamp") java.time.LocalDateTime timestamp);

  /**
   * Updates the lastPriceUpdate timestamp for a product.
   *
   * @param productId the product ID
   * @param timestamp the timestamp to set
   */
  @Modifying
  @Query("UPDATE ProductEntity p SET p.lastPriceUpdate = :timestamp WHERE p.id = :productId")
  void updateLastPriceUpdate(
      @Param("productId") Long productId, @Param("timestamp") java.time.LocalDateTime timestamp);
}
