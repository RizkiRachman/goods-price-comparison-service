package com.example.goodsprice.receipt.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.util.JsonUtils;
import com.example.goodsprice.receipt.application.domain.model.ReceiptCreateDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptItemDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptStatus;
import com.example.goodsprice.receipt.application.port.out.ReceiptEventOutPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

  @Mock private ReceiptRepositoryPort receiptRepository;
  @Mock private ReceiptEventOutPort eventOutPort;
  @Mock private com.example.goodsprice.llm.application.port.out.LlmProviderPort llmProvider;
  @Mock private ObjectMapper objectMapper;

  @InjectMocks private ReceiptService receiptService;

  @Captor private ArgumentCaptor<ReceiptDomain> receiptCaptor;

  private ReceiptCreateDomain createRequest;
  private final Map<UUID, ReceiptDomain> store = new HashMap<>();

  @BeforeEach
  void setUp() {
    store.clear();
    var item =
        ReceiptItemDomain.builder()
            .productName("Apple")
            .category("Fruit")
            .quantity(2.0)
            .unitPrice(5.0)
            .totalPrice(10.0)
            .unit("KG")
            .build();

    createRequest =
        ReceiptCreateDomain.builder()
            .storeName("Toko Segar")
            .storeLocation("Jakarta")
            .receiptDate("2026-05-08")
            .totalAmount(new BigDecimal("10.00"))
            .items(List.of(item))
            .build();
  }

  @Test
  void shouldCreateReceiptSuccessfully() {
    mockSaveWithStore();
    var result = receiptService.create(createRequest);

    verify(receiptRepository, times(2)).save(receiptCaptor.capture());
    var saved = receiptCaptor.getAllValues().get(0);

    assertEquals("Toko Segar", saved.getStoreName());
    assertEquals("Jakarta", saved.getStoreLocation());
    assertEquals("2026-05-08", saved.getReceiptDate());
    assertEquals(0, new BigDecimal("10.00").compareTo(saved.getTotalAmount()));
    assertNotNull(saved.getExtractedDataJson());
    assertEquals(JsonUtils.toJson(createRequest), saved.getExtractedDataJson());
    var expectedImageHash = JsonUtils.hash256(createRequest);
    assertEquals(expectedImageHash, saved.getImageHash());

    var approved = receiptCaptor.getAllValues().get(1);
    assertEquals(ReceiptStatus.APPROVED, approved.getStatus());

    verify(eventOutPort).publishReceiptApproved(approved);
    assertNotNull(approved.getId());

    assertEquals(ReceiptStatus.APPROVED, result.getStatus());
    assertEquals("Toko Segar", result.getStoreName());
    assertEquals(0, new BigDecimal("10.00").compareTo(result.getTotalAmount()));
  }

  @Test
  void shouldCreateReceiptWithEmptyItems() {
    createRequest.setItems(List.of());
    mockSaveWithStore();
    var result = receiptService.create(createRequest);

    verify(receiptRepository, times(2)).save(receiptCaptor.capture());
    var saved = receiptCaptor.getAllValues().get(0);
    assertEquals(JsonUtils.toJson(createRequest), saved.getExtractedDataJson());
    assertEquals("Toko Segar", saved.getStoreName());
    var expectedImageHash = JsonUtils.hash256(createRequest);
    assertEquals(expectedImageHash, saved.getImageHash());

    assertEquals(ReceiptStatus.APPROVED, result.getStatus());
    assertNotNull(result.getId());
  }

  @Test
  void shouldCreateReceiptWithNullItems() {
    createRequest.setItems(null);
    mockSaveWithStore();
    var result = receiptService.create(createRequest);

    verify(receiptRepository, times(2)).save(receiptCaptor.capture());
    var saved = receiptCaptor.getAllValues().get(0);
    assertEquals(JsonUtils.toJson(createRequest), saved.getExtractedDataJson());
    assertNotNull(saved.getImageHash());

    assertEquals(ReceiptStatus.APPROVED, result.getStatus());
    assertNotNull(result.getId());
  }

  @Test
  void shouldCreateReceiptWithNullTotalAmount() {
    createRequest.setTotalAmount(null);
    mockSaveWithStore();
    var result = receiptService.create(createRequest);

    verify(receiptRepository, times(2)).save(receiptCaptor.capture());
    var saved = receiptCaptor.getAllValues().get(0);
    assertEquals(null, saved.getTotalAmount());

    assertEquals(ReceiptStatus.APPROVED, result.getStatus());
    assertEquals(null, result.getTotalAmount());
  }

  @Test
  void shouldCreateReceiptWithoutStoreLocation() {
    createRequest.setStoreLocation(null);
    mockSaveWithStore();
    var result = receiptService.create(createRequest);

    verify(receiptRepository, times(2)).save(receiptCaptor.capture());
    var saved = receiptCaptor.getAllValues().get(0);
    assertEquals(null, saved.getStoreLocation());
    assertNotNull(saved.getImageHash());

    assertEquals(ReceiptStatus.APPROVED, result.getStatus());
    assertEquals(null, result.getStoreLocation());
  }

  @Test
  void shouldCreateReceiptWithMultipleItems() {
    var item1 =
        ReceiptItemDomain.builder()
            .productName("Apple")
            .category("Fruit")
            .quantity(2.0)
            .unitPrice(5.0)
            .totalPrice(10.0)
            .unit("KG")
            .build();
    var item2 =
        ReceiptItemDomain.builder()
            .productName("Milk")
            .category("Dairy")
            .quantity(1.0)
            .unitPrice(15.0)
            .totalPrice(15.0)
            .unit("LITER")
            .build();
    createRequest.setItems(List.of(item1, item2));

    mockSaveWithStore();
    var result = receiptService.create(createRequest);

    verify(receiptRepository, times(2)).save(receiptCaptor.capture());
    var saved = receiptCaptor.getAllValues().get(0);
    assertNotNull(saved.getExtractedDataJson());
    assertEquals("Toko Segar", saved.getStoreName());
    assertNotNull(saved.getImageHash());
    verify(eventOutPort).publishReceiptApproved(any());

    assertEquals(ReceiptStatus.APPROVED, result.getStatus());
    assertEquals("Toko Segar", result.getStoreName());
    assertNotNull(result.getId());
  }

  @Test
  void shouldFindReceiptById() {
    var id = UUID.randomUUID();
    var receipt = ReceiptDomain.builder().id(id).storeName("Toko Segar").build();
    when(receiptRepository.findById(id)).thenReturn(receipt);

    var result = receiptService.findById(id);

    assertNotNull(result);
    assertEquals("Toko Segar", result.getStoreName());
  }

  @Test
  void shouldThrowNotFoundWhenReceiptNotFound() {
    var id = UUID.randomUUID();
    when(receiptRepository.findById(id)).thenReturn(null);

    assertThrows(
        com.example.goodsprice.common.exception.NotFoundException.class,
        () -> receiptService.findById(id));
  }

  @Test
  void shouldReturnStatusWhenReceiptExists() {
    var id = UUID.randomUUID();
    var receipt = ReceiptDomain.builder().id(id).status(ReceiptStatus.APPROVED).build();
    when(receiptRepository.findById(id)).thenReturn(receipt);

    var result = receiptService.getStatus(id);

    assertEquals(ReceiptStatus.APPROVED, result);
  }

  @Test
  void shouldThrowNotFoundWhenReceiptDoesNotExistInGetStatus() {
    var id = UUID.randomUUID();
    when(receiptRepository.findById(id)).thenReturn(null);

    assertThrows(
        com.example.goodsprice.common.exception.NotFoundException.class,
        () -> receiptService.getStatus(id));
  }

  @Test
  void shouldApproveReceipt() {
    var id = UUID.randomUUID();
    var receipt = ReceiptDomain.builder().id(id).status(ReceiptStatus.PENDING).build();
    when(receiptRepository.findById(id)).thenReturn(receipt);
    when(receiptRepository.save(any(ReceiptDomain.class))).thenReturn(receipt);

    receiptService.approve(id);

    verify(receiptRepository).save(any(ReceiptDomain.class));
    verify(eventOutPort).publishReceiptApproved(receipt);
  }

  @Test
  void shouldRejectReceipt() {
    var id = UUID.randomUUID();
    var receipt = ReceiptDomain.builder().id(id).status(ReceiptStatus.PENDING).build();
    when(receiptRepository.findById(id)).thenReturn(receipt);
    when(receiptRepository.save(any(ReceiptDomain.class))).thenReturn(receipt);

    receiptService.reject(id);

    verify(receiptRepository).save(any(ReceiptDomain.class));
  }

  // ===== Positive cases for upload, process, insertItems =====

  @Test
  void shouldUploadReceipt() {
    var imageBytes = "test-image".getBytes();
    var receipt =
        ReceiptDomain.builder().id(UUID.randomUUID()).status(ReceiptStatus.PENDING).build();
    when(receiptRepository.findByImageHash(any())).thenReturn(null);
    when(receiptRepository.save(any(ReceiptDomain.class))).thenReturn(receipt);

    var result = receiptService.upload(imageBytes, "receipt.jpg");

    assertNotNull(result);
    assertEquals(ReceiptStatus.PENDING, result.getStatus());
    verify(receiptRepository).save(any(ReceiptDomain.class));
    verify(eventOutPort).publishReceiptUploaded(any());
  }

  @Test
  void shouldProcessReceiptWithValidLlmResponse() throws Exception {
    var id = UUID.randomUUID();
    var receipt = ReceiptDomain.builder().id(id).build();
    Map<String, Object> llmData = new HashMap<>();
    llmData.put("storeName", "Toko Segar");
    llmData.put("totalAmount", "15000");

    when(receiptRepository.findById(id)).thenReturn(receipt);
    when(receiptRepository.save(any(ReceiptDomain.class))).thenReturn(receipt);
    when(llmProvider.extractReceiptData(any())).thenReturn(llmData);
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    receiptService.process(id, "data".getBytes());

    verify(receiptRepository, times(2)).save(any(ReceiptDomain.class));
    verify(eventOutPort).publishReceiptProcessed(any());
  }

  @Test
  void shouldInsertItemsIntoReceipt() {
    var id = UUID.randomUUID();
    var receipt = ReceiptDomain.builder().id(id).build();
    List<Map<String, Object>> items = List.of(new HashMap<>(Map.of("productName", "Apple")));

    when(receiptRepository.findById(id)).thenReturn(receipt);
    when(receiptRepository.save(any(ReceiptDomain.class))).thenReturn(receipt);

    receiptService.insertItems(id, items);

    verify(receiptRepository).save(any(ReceiptDomain.class));
  }

  @Test
  void shouldRejectDuplicateUpload() {
    var existing =
        ReceiptDomain.builder().id(UUID.randomUUID()).status(ReceiptStatus.COMPLETED).build();
    when(receiptRepository.findByImageHash(any())).thenReturn(existing);

    var result = receiptService.upload("data".getBytes(), "dup.jpg");

    assertNotNull(result);
    assertEquals(existing.getId(), result.getId());
    verify(receiptRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenInsertItemsIsEmpty() {
    var id = UUID.randomUUID();
    var receipt = ReceiptDomain.builder().id(id).build();
    when(receiptRepository.findById(id)).thenReturn(receipt);

    assertThrows(IllegalArgumentException.class, () -> receiptService.insertItems(id, List.of()));
  }

  @Test
  void shouldHandleProcessLlmFailure() {
    var id = UUID.randomUUID();
    var receipt = ReceiptDomain.builder().id(id).build();
    when(receiptRepository.findById(id)).thenReturn(receipt);
    when(receiptRepository.save(any(ReceiptDomain.class))).thenReturn(receipt);
    when(llmProvider.extractReceiptData(any())).thenThrow(new RuntimeException("LLM error"));

    receiptService.process(id, "data".getBytes());

    verify(receiptRepository, times(2)).save(any(ReceiptDomain.class));
    verify(eventOutPort, never()).publishReceiptProcessed(any());
  }

  private void mockSaveWithStore() {
    when(receiptRepository.save(any()))
        .thenAnswer(
            invocation -> {
              var receipt = invocation.<ReceiptDomain>getArgument(0);
              if (receipt.getId() == null) {
                receipt.setId(UUID.randomUUID());
              }
              store.put(receipt.getId(), receipt);
              return receipt;
            });
    when(receiptRepository.findById(any()))
        .thenAnswer(
            invocation -> {
              var id = invocation.<UUID>getArgument(0);
              return store.get(id);
            });
  }
}
