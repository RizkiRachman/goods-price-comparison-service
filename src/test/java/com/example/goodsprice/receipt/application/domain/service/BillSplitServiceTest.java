package com.example.goodsprice.receipt.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.receipt.application.domain.model.BillSplitOrderDetailDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitParticipantDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitRequestDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitRequestOrderDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitType;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptItemDomainRepositoryPort;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BillSplitServiceTest {

  @Mock private ReceiptInPort receiptInPort;
  @Mock private ReceiptItemRepositoryPort receiptItemRepository;

  @InjectMocks private BillSplitService billSplitService;

  private final UUID receiptId = UUID.randomUUID();

  private ReceiptDomain receipt;

  @BeforeEach
  void setUp() {
    receipt = ReceiptDomain.builder().id(receiptId).totalAmount(new BigDecimal("100.00")).build();
  }

  @Nested
  class RatioSplit {

    @Test
    void shouldSplitBillEquallyAmongParticipants() {
      when(receiptInPort.findById(receiptId)).thenReturn(receipt);
      var request =
          BillSplitRequestDomain.builder()
              .type(BillSplitType.RATIO)
              .numberOfParticipants(4)
              .build();

      var result = billSplitService.splitBill(receiptId, request);

      assertNotNull(result);
      assertEquals(receiptId, result.getReceiptId());
      assertEquals(BillSplitType.RATIO, result.getType());
      assertEquals(4, result.getNumberOfParticipants());
      assertEquals(100.0, result.getTotalAmount());
      assertEquals(0.0, result.getUnassignedTotal());

      var participants = result.getParticipants();
      assertEquals(4, participants.size());
      for (var p : participants) {
        assertEquals(25.0, p.getSubtotal());
        assertEquals(List.of(), p.getItems());
      }
      assertEquals("Participant 1", participants.get(0).getName());
      assertEquals("Participant 2", participants.get(1).getName());
      assertEquals("Participant 3", participants.get(2).getName());
      assertEquals("Participant 4", participants.get(3).getName());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenReceiptNotFound() {
      when(receiptInPort.findById(receiptId)).thenReturn(null);
      var request =
          BillSplitRequestDomain.builder()
              .type(BillSplitType.RATIO)
              .numberOfParticipants(2)
              .build();

      assertThrows(NotFoundException.class, () -> billSplitService.splitBill(receiptId, request));
    }

    @Test
    void shouldHandleNullTotalAmount() {
      receipt.setTotalAmount(null);
      when(receiptInPort.findById(receiptId)).thenReturn(receipt);
      var request =
          BillSplitRequestDomain.builder()
              .type(BillSplitType.RATIO)
              .numberOfParticipants(3)
              .build();

      var result = billSplitService.splitBill(receiptId, request);

      assertEquals(0.0, result.getTotalAmount());
      assertEquals(3, result.getParticipants().size());
      result.getParticipants().forEach(p -> assertEquals(0.0, p.getSubtotal()));
    }

    @Test
    void shouldHandleZeroParticipants() {
      when(receiptInPort.findById(receiptId)).thenReturn(receipt);
      var request =
          BillSplitRequestDomain.builder()
              .type(BillSplitType.RATIO)
              .numberOfParticipants(0)
              .build();

      var result = billSplitService.splitBill(receiptId, request);

      assertEquals(0, result.getParticipants().size());
      assertEquals(0.0, result.getUnassignedTotal());
    }

    @Test
    void shouldHandleSingleParticipant() {
      when(receiptInPort.findById(receiptId)).thenReturn(receipt);
      var request =
          BillSplitRequestDomain.builder()
              .type(BillSplitType.RATIO)
              .numberOfParticipants(1)
              .build();

      var result = billSplitService.splitBill(receiptId, request);

      assertEquals(1, result.getParticipants().size());
      assertEquals(100.0, result.getParticipants().get(0).getSubtotal());
      assertEquals("Participant 1", result.getParticipants().get(0).getName());
    }
  }

  @Nested
  class SelectionSplit {

    @Test
    void shouldSplitBillBySelectionWithMatchingItems() {
      when(receiptInPort.findById(receiptId)).thenReturn(receipt);
      when(receiptItemRepository.findByReceiptId(receiptId))
          .thenReturn(
              List.of(
                  ReceiptItemDomain.builder()
                      .productName("Apple")
                      .unitPrice(10.0)
                      .quantity(2.0)
                      .build(),
                  ReceiptItemDomain.builder()
                      .productName("Milk")
                      .unitPrice(5.0)
                      .quantity(1.0)
                      .build()));

      var request =
          BillSplitRequestDomain.builder()
              .type(BillSplitType.SELECTION)
              .numberOfParticipants(2)
              .orders(
                  List.of(
                      BillSplitRequestOrderDomain.builder()
                          .name("Alice")
                          .orders(
                              List.of(
                                  BillSplitOrderDetailDomain.builder()
                                      .name("Apple")
                                      .productId(1L)
                                      .quantity(1.0)
                                      .build()))
                          .build(),
                      BillSplitRequestOrderDomain.builder()
                          .name("Bob")
                          .orders(
                              List.of(
                                  BillSplitOrderDetailDomain.builder()
                                      .name("Milk")
                                      .productId(2L)
                                      .quantity(2.0)
                                      .build()))
                          .build()))
              .build();

      var result = billSplitService.splitBill(receiptId, request);

      assertEquals(BillSplitType.SELECTION, result.getType());
      assertEquals(2, result.getNumberOfParticipants());
      assertEquals(100.0, result.getTotalAmount());

      var participants = result.getParticipants();
      assertEquals(2, participants.size());

      var alice = findParticipant(participants, "Alice");
      assertNotNull(alice);
      assertEquals(10.0, alice.getSubtotal());
      assertEquals(1, alice.getItems().size());
      var aliceItem = alice.getItems().get(0);
      assertEquals("Apple", aliceItem.getProductName());
      assertEquals(1.0, aliceItem.getQuantity());
      assertEquals(10.0, aliceItem.getUnitPrice());
      assertEquals(10.0, aliceItem.getSubtotal());

      var bob = findParticipant(participants, "Bob");
      assertNotNull(bob);
      assertEquals(10.0, bob.getSubtotal());
      assertEquals(1, bob.getItems().size());
      var bobItem = bob.getItems().get(0);
      assertEquals("Milk", bobItem.getProductName());
      assertEquals(2.0, bobItem.getQuantity());
      assertEquals(5.0, bobItem.getUnitPrice());
      assertEquals(10.0, bobItem.getSubtotal());
    }

    @Test
    void shouldHandleUnassignedParticipants() {
      when(receiptInPort.findById(receiptId)).thenReturn(receipt);
      when(receiptItemRepository.findByReceiptId(receiptId))
          .thenReturn(
              List.of(
                  ReceiptItemDomain.builder()
                      .productName("Apple")
                      .unitPrice(10.0)
                      .quantity(2.0)
                      .build()));

      var request =
          BillSplitRequestDomain.builder()
              .type(BillSplitType.SELECTION)
              .numberOfParticipants(3)
              .orders(
                  List.of(
                      BillSplitRequestOrderDomain.builder()
                          .name("Alice")
                          .orders(
                              List.of(
                                  BillSplitOrderDetailDomain.builder()
                                      .name("Apple")
                                      .productId(1L)
                                      .quantity(1.0)
                                      .build()))
                          .build()))
              .build();

      var result = billSplitService.splitBill(receiptId, request);

      var participants = result.getParticipants();
      assertEquals(3, participants.size());

      var alice = findParticipant(participants, "Alice");
      assertEquals(10.0, alice.getSubtotal());

      var unnamed =
          participants.stream().filter(p -> p.getName().startsWith("Participant")).toList();
      assertEquals(2, unnamed.size());
      assertEquals(90.0, result.getUnassignedTotal());
      unnamed.forEach(p -> assertEquals(45.0, p.getSubtotal()));
    }

    @Test
    void shouldHandleNullOrders() {
      when(receiptInPort.findById(receiptId)).thenReturn(receipt);
      when(receiptItemRepository.findByReceiptId(receiptId)).thenReturn(List.of());

      var request =
          BillSplitRequestDomain.builder()
              .type(BillSplitType.SELECTION)
              .numberOfParticipants(2)
              .build();

      var result = billSplitService.splitBill(receiptId, request);

      assertEquals(2, result.getParticipants().size());
      assertEquals(100.0, result.getUnassignedTotal());
      result.getParticipants().forEach(p -> assertEquals(50.0, p.getSubtotal()));
    }

    @Test
    void shouldHandleEmptyOrders() {
      when(receiptInPort.findById(receiptId)).thenReturn(receipt);
      when(receiptItemRepository.findByReceiptId(receiptId)).thenReturn(List.of());

      var request =
          BillSplitRequestDomain.builder()
              .type(BillSplitType.SELECTION)
              .numberOfParticipants(2)
              .orders(List.of())
              .build();

      var result = billSplitService.splitBill(receiptId, request);

      assertEquals(2, result.getParticipants().size());
      assertEquals(100.0, result.getUnassignedTotal());
      result.getParticipants().forEach(p -> assertEquals(50.0, p.getSubtotal()));
    }

    @Test
    void shouldSkipUnmatchedOrderDetailNames() {
      when(receiptInPort.findById(receiptId)).thenReturn(receipt);
      when(receiptItemRepository.findByReceiptId(receiptId))
          .thenReturn(
              List.of(
                  ReceiptItemDomain.builder()
                      .productName("Apple")
                      .unitPrice(10.0)
                      .quantity(2.0)
                      .build()));

      var request =
          BillSplitRequestDomain.builder()
              .type(BillSplitType.SELECTION)
              .numberOfParticipants(1)
              .orders(
                  List.of(
                      BillSplitRequestOrderDomain.builder()
                          .name("Alice")
                          .orders(
                              List.of(
                                  BillSplitOrderDetailDomain.builder()
                                      .name("NonExistentProduct")
                                      .productId(99L)
                                      .quantity(1.0)
                                      .build()))
                          .build()))
              .build();

      var result = billSplitService.splitBill(receiptId, request);

      var participant = result.getParticipants().get(0);
      assertEquals("Alice", participant.getName());
      assertEquals(0, participant.getItems().size());
      assertEquals(0.0, participant.getSubtotal());
      assertEquals(100.0, result.getUnassignedTotal());
    }

    @Test
    void shouldHandleNullQuantity() {
      when(receiptInPort.findById(receiptId)).thenReturn(receipt);
      when(receiptItemRepository.findByReceiptId(receiptId))
          .thenReturn(
              List.of(
                  ReceiptItemDomain.builder()
                      .productName("Apple")
                      .unitPrice(10.0)
                      .quantity(2.0)
                      .build()));

      var request =
          BillSplitRequestDomain.builder()
              .type(BillSplitType.SELECTION)
              .numberOfParticipants(1)
              .orders(
                  List.of(
                      BillSplitRequestOrderDomain.builder()
                          .name("Alice")
                          .orders(
                              List.of(
                                  BillSplitOrderDetailDomain.builder()
                                      .name("Apple")
                                      .productId(1L)
                                      .build()))
                          .build()))
              .build();

      var result = billSplitService.splitBill(receiptId, request);

      var participant = result.getParticipants().get(0);
      assertEquals(1, participant.getItems().size());
      assertEquals(0.0, participant.getSubtotal());
      var item = participant.getItems().get(0);
      assertEquals(0.0, item.getQuantity());
      assertEquals(0.0, item.getSubtotal());
    }

    private BillSplitParticipantDomain findParticipant(
        List<BillSplitParticipantDomain> participants, String name) {
      return participants.stream().filter(p -> name.equals(p.getName())).findFirst().orElse(null);
    }
  }
}
