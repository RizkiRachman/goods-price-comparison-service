package com.example.goodsprice.receipt.infrastructure.adapter.persistence;

import com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity.ReceiptEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaReceiptRepository extends JpaRepository<ReceiptEntity, UUID> {

  Optional<ReceiptEntity> findByImageHash(String imageHash);

  boolean existsByImageHash(String imageHash);
}
