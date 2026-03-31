package com.example.goodsprice.controller;

import com.example.goodsprice.api.controller.ReceiptsApi;
import com.example.goodsprice.api.model.ReceiptItem;
import com.example.goodsprice.api.model.ReceiptResultResponse;
import com.example.goodsprice.api.model.ReceiptStatusResponse;
import com.example.goodsprice.api.model.ReceiptUploadResponse;
import com.example.goodsprice.service.receipt.ReceiptService;
import lombok.RequiredArgsConstructor;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Controller for receipt upload and OCR processing.
 * Integrates with Gemini LLM for receipt extraction.
 */
@RestController
@RequiredArgsConstructor
public class ReceiptController implements ReceiptsApi {

    private final ReceiptService receiptService;

    // Simple in-memory storage for receipt results
    private final Map<UUID, Map<String, Object>> receiptStorage = new ConcurrentHashMap<>();

    @Override
    public ResponseEntity<ReceiptUploadResponse> uploadReceipt(MultipartFile image) {
        // Process receipt immediately using Gemini
        Map<String, Object> result = receiptService.processReceipt(image);

        // Store result
        UUID receiptId = UUID.fromString((String) result.get("receiptId"));
        receiptStorage.put(receiptId, result);

        // Build response
        ReceiptUploadResponse response = new ReceiptUploadResponse();
        response.setJobId(receiptId);
        response.setStatus(ReceiptUploadResponse.StatusEnum.COMPLETED);

        return ResponseEntity.accepted().body(response);
    }

    @Override
    public ResponseEntity<ReceiptStatusResponse> getReceiptStatus(UUID id) {
        ReceiptStatusResponse response = new ReceiptStatusResponse();
        response.setJobId(id);

        if (id != null && receiptStorage.containsKey(id)) {
            response.setStatus(ReceiptStatusResponse.StatusEnum.COMPLETED);
        } else {
            response.setStatus(ReceiptStatusResponse.StatusEnum.PROCESSING);
        }

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ReceiptResultResponse> getReceiptResults(UUID id) {
        ReceiptResultResponse response = new ReceiptResultResponse();
        response.setJobId(id);

        if (id != null) {
            Map<String, Object> result = receiptStorage.get(id);
            if (result != null) {
                // Map the result data to response
                response.setStoreName((String) result.get("storeName"));

                // Parse date string to LocalDate
                String dateStr = (String) result.get("date");
                if (dateStr != null && !dateStr.isEmpty()) {
                    try {
                        response.setDate(LocalDate.parse(dateStr));
                    } catch (DateTimeParseException e) {
                        // Try common date formats
                        response.setDate(parseDateFlexible(dateStr));
                    }
                }
                
                // Map total amount
                Object totalAmount = result.get("totalAmount");
                if (totalAmount instanceof Number) {
                    response.setTotalAmount(((Number) totalAmount).doubleValue());
                }
                
                // Map items list
                Object itemsObj = result.get("items");
                if (itemsObj instanceof List) {
                    //noinspection unchecked
                    List<Map<String, Object>> items = (List<Map<String, Object>>) itemsObj;
                    List<ReceiptItem> receiptItems = items.stream()
                        .map(this::mapToReceiptItem)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                    response.setItems(receiptItems);
                }
            }
        }

        return ResponseEntity.ok(response);
    }
    
    /**
     * Map an item map to ReceiptItem DTO.
     */
    private ReceiptItem mapToReceiptItem(Map<String, Object> itemData) {
        if (itemData == null) {
            return null;
        }
        
        ReceiptItem item = new ReceiptItem();
        item.setProductName((String) itemData.get("productName"));
        
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
