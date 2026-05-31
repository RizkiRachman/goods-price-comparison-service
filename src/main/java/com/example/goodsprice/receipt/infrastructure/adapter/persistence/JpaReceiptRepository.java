package com.example.goodsprice.receipt.infrastructure.adapter.persistence;

import com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity.ReceiptEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaReceiptRepository extends JpaRepository<ReceiptEntity, UUID> {

  Optional<ReceiptEntity> findByImageHash(String imageHash);

  boolean existsByImageHash(String imageHash);

  @Modifying
  @Query("UPDATE ReceiptEntity r SET r.imageData = :imageData WHERE r.id = :id")
  void updateImageData(@Param("id") UUID id, @Param("imageData") byte[] imageData);
}
