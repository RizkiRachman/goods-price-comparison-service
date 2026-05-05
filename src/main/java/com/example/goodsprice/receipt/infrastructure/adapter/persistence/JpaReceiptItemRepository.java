package com.example.goodsprice.receipt.infrastructure.adapter.persistence;

import com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity.ReceiptItemEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaReceiptItemRepository extends JpaRepository<ReceiptItemEntity, Long> {

  List<ReceiptItemEntity> findByReceiptId(UUID receiptId);
}
