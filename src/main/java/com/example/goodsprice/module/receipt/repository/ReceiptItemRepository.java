package com.example.goodsprice.module.receipt.repository;

import com.example.goodsprice.module.receipt.entity.ReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Long> {

    List<ReceiptItem> findByReceiptId(UUID receiptId);

    List<ReceiptItem> findByProductNameContainingIgnoreCase(String productName);
}
