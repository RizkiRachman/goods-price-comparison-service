package com.example.goodsprice.controller;

import com.example.goodsprice.api.controller.ReceiptsApi;
import com.example.goodsprice.api.model.ReceiptItem;
import com.example.goodsprice.api.model.ReceiptResultResponse;
import com.example.goodsprice.api.model.ReceiptStatusResponse;
import com.example.goodsprice.api.model.ReceiptUploadResponse;
import com.example.goodsprice.service.receipt.ReceiptResult;
import com.example.goodsprice.service.receipt.ReceiptService;
import com.example.goodsprice.service.receipt.ReceiptStatus;
import com.example.goodsprice.service.receipt.ReceiptUploadData;
import com.example.goodsprice.service.receipt.event.ReceiptProcessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for receipt upload and async processing.
 * Returns immediately with job ID, processing happens in background.
 */
@RestController
@RequiredArgsConstructor
public class ReceiptController implements ReceiptsApi {

    private final ReceiptService receiptService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ResponseEntity<ReceiptUploadResponse> uploadReceipt(MultipartFile image) {
        // Upload receipt (transaction commits here)
        ReceiptUploadData data = receiptService.uploadReceipt(image);

        // Build response
        ReceiptUploadResponse response = new ReceiptUploadResponse();
        response.setJobId(data.getReceiptId());
        response.setStatus(mapStatus(data.getStatus()));

        // If duplicate, don't process again - return existing result
        if (data.isDuplicate()) {
            return ResponseEntity.ok(response);
        }

        // Fire async event AFTER transaction commits
        // This ensures receipt is visible to the listener
        eventPublisher.publishEvent(new ReceiptProcessEvent(
                this, data.getReceiptId(), data.getImageBytes(), data.getOriginalFilename()));

        return ResponseEntity.accepted().body(response);
    }

    @Override
    public ResponseEntity<ReceiptStatusResponse> getReceiptStatus(UUID id) {
        ReceiptStatusResponse response = new ReceiptStatusResponse();
        response.setJobId(id);

        ReceiptResult result = receiptService.getReceiptResult(id);
        
        if (result.getStatus() == null) {
            response.setStatus(ReceiptStatusResponse.StatusEnum.PROCESSING);
        } else {
            response.setStatus(mapStatusForStatusResponse(result.getStatus()));
        }

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ReceiptResultResponse> getReceiptResults(UUID id) {
        ReceiptResultResponse response = new ReceiptResultResponse();
        response.setJobId(id);

        ReceiptResult result = receiptService.getReceiptResult(id);

        if (result.getStatus() != null) {
            response.setStoreName(result.getStoreName());
            response.setStoreLocation(result.getStoreLocation());

            // Parse date
            if (result.getDate() != null) {
                response.setDate(parseDateFlexible(result.getDate()));
            }

            response.setTotalAmount(result.getTotalAmount());

            // Map items from result
            List<Map<String, Object>> items = result.getItems();
            if (items != null && !items.isEmpty()) {
                List<ReceiptItem> receiptItems = items.stream()
                        .map(this::mapToReceiptItem)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                response.setItems(receiptItems);
            }
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Map item data to ReceiptItem DTO.
     */
    private ReceiptItem mapToReceiptItem(Map<String, Object> itemData) {
        if (itemData == null) {
            return null;
        }

        ReceiptItem item = new ReceiptItem();
        item.setProductName((String) itemData.get("productName"));
        item.setCategory((String) itemData.get("category"));
        item.setUnit((String) itemData.get("unit"));

        // Map numeric fields
        Object quantity = itemData.get("quantity");
        if (quantity instanceof Number) {
            item.setQuantity(((Number) quantity).doubleValue());
        }

        Object unitPrice = itemData.get("unitPrice");
        if (unitPrice instanceof Number) {
            item.setUnitPrice(((Number) unitPrice).doubleValue());
        }

        Object totalPrice = itemData.get("totalPrice");
        if (totalPrice instanceof Number) {
            item.setTotalPrice(((Number) totalPrice).doubleValue());
        }

        return item;
    }
    
    /**
     * Map internal status to API status enum.
     */
    private ReceiptUploadResponse.StatusEnum mapStatus(ReceiptStatus status) {
        return switch (status) {
            case PENDING -> ReceiptUploadResponse.StatusEnum.PROCESSING;
            case PROCESSING -> ReceiptUploadResponse.StatusEnum.PROCESSING;
            case COMPLETED -> ReceiptUploadResponse.StatusEnum.COMPLETED;
            case FAILED -> ReceiptUploadResponse.StatusEnum.FAILED;
        };
    }
    
    private ReceiptStatusResponse.StatusEnum mapStatusForStatusResponse(ReceiptStatus status) {
        if (status == null) {
            return ReceiptStatusResponse.StatusEnum.PROCESSING;
        }
        return switch (status) {
            case PENDING -> ReceiptStatusResponse.StatusEnum.PROCESSING;
            case PROCESSING -> ReceiptStatusResponse.StatusEnum.PROCESSING;
            case COMPLETED -> ReceiptStatusResponse.StatusEnum.COMPLETED;
            case FAILED -> ReceiptStatusResponse.StatusEnum.FAILED;
        };
    }
    
    /**
     * Try to parse date using multiple common formats.
     */
    private LocalDate parseDateFlexible(String dateStr) {
        String[] formats = {"dd/MM/yyyy", "MM/dd/yyyy", "dd-MM-yyyy", "yyyy-MM-dd"};
        for (String format : formats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException ignored) {
                // Try next format
            }
        }
        return null;
    }
}
