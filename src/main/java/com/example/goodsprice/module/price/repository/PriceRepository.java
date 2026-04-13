package com.example.goodsprice.module.price.repository;

import com.example.goodsprice.module.price.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    List<Price> findByProductIdAndStoreId(Long productId, Long storeId);

    List<Price> findByProductId(Long productId);

    List<Price> findByStoreId(Long storeId);

    @Query("SELECT p FROM Price p WHERE p.product.id = :productId AND p.dateRecorded BETWEEN :startDate AND :endDate ORDER BY p.dateRecorded ASC")
    List<Price> findByProductIdAndDateRange(@Param("productId") Long productId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM Price p WHERE p.product.id = :productId ORDER BY p.price ASC")
    List<Price> findCheapestByProductId(@Param("productId") Long productId);
}
