package com.example.goodsprice.module.receipt.repository;

import com.example.goodsprice.module.receipt.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Receipt entities.
 */
@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {
    
    /**
     * Find receipt by image hash for deduplication.
     */
    Optional<Receipt> findByImageHash(String imageHash);
    
    /**
     * Check if receipt exists with given image hash.
     */
    boolean existsByImageHash(String imageHash);
}
