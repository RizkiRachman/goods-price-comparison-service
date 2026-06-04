package com.example.goodsprice.receipt.application.domain.service;

import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.receipt.application.domain.model.BillSplitItemDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitParticipantDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitRequestDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitRequestOrderDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitResponseDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitType;
import com.example.goodsprice.receipt.application.port.in.BillSplitInPort;
import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptItemRepositoryPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillSplitService implements BillSplitInPort {

  private final ReceiptInPort receiptInPort;
  private final ReceiptItemRepositoryPort receiptItemRepository;

  @Override
  @Cacheable(value = "bill-splits", key = "#receiptId + '-' + #request.hashCode()")
  public BillSplitResponseDomain splitBill(UUID receiptId, BillSplitRequestDomain request) {
    var receipt = receiptInPort.findById(receiptId);
    if (Objects.isNull(receipt)) {
      throw new NotFoundException(
          ErrorCodes.RECEIPT_NOT_FOUND, "Receipt not found with id: %s".formatted(receiptId));
    }

    var totalAmount =
        Objects.nonNull(receipt.getTotalAmount()) ? receipt.getTotalAmount().doubleValue() : 0.0;
    var response =
        BillSplitResponseDomain.builder()
            .receiptId(receiptId)
            .type(request.getType())
            .numberOfParticipants(request.getNumberOfParticipants())
            .totalAmount(totalAmount)
            .build();

    if (BillSplitType.RATIO == request.getType()) {
      handleRatioSplit(response, request);
    } else {
      handleSelectionSplit(response, request);
    }

    return response;
  }

  private void handleRatioSplit(BillSplitResponseDomain response, BillSplitRequestDomain request) {
    var totalAmount = response.getTotalAmount();
    var count = request.getNumberOfParticipants();
    var sharePerPerson = count > 0 ? totalAmount / count : 0.0;

    var participants = new ArrayList<BillSplitParticipantDomain>();
    for (int i = 1; i <= count; i++) {
      participants.add(
          BillSplitParticipantDomain.builder()
              .name("Participant %d".formatted(i))
              .items(List.of())
              .subtotal(sharePerPerson)
              .build());
    }
    response.setParticipants(participants);
    response.setUnassignedTotal(0.0);
  }

  private void handleSelectionSplit(
      BillSplitResponseDomain response, BillSplitRequestDomain request) {
    var receiptId = response.getReceiptId();
    var receiptItems = receiptItemRepository.findByReceiptId(receiptId);

    var orders =
        Objects.nonNull(request.getOrders())
            ? request.getOrders()
            : List.<BillSplitRequestOrderDomain>of();

    var participants = new ArrayList<BillSplitParticipantDomain>();
    double totalAssigned = 0.0;

    for (var entry : orders) {
      var participantItems = new ArrayList<BillSplitItemDomain>();
      double participantSubtotal = 0.0;

      for (var order : entry.getOrders()) {
        var productName = order.getName();
        var matchedItem =
            receiptItems.stream()
                .filter(i -> Objects.equals(i.getProductName(), productName))
                .findFirst()
                .orElse(null);
        if (Objects.isNull(matchedItem)) continue;

        var qty = Objects.nonNull(order.getQuantity()) ? order.getQuantity() : 0.0;
        var unitPrice =
            Objects.nonNull(matchedItem.getUnitPrice()) ? matchedItem.getUnitPrice() : 0.0;
        var subtotal = qty * unitPrice;

        participantItems.add(
            BillSplitItemDomain.builder()
                .productId(order.getProductId())
                .productName(productName)
                .quantity(qty)
                .unitPrice(unitPrice)
                .subtotal(subtotal)
                .build());
        participantSubtotal += subtotal;
      }

      totalAssigned += participantSubtotal;
      participants.add(
          BillSplitParticipantDomain.builder()
              .name(entry.getName())
              .items(participantItems)
              .subtotal(participantSubtotal)
              .build());
    }

    var namedCount = orders.size();
    var participantCount = request.getNumberOfParticipants();
    var unassignedTotal = Math.max(0.0, response.getTotalAmount() - totalAssigned);
    response.setUnassignedTotal(unassignedTotal);

    var unassignedParticipants = participantCount - namedCount;
    for (int i = 0; i < unassignedParticipants; i++) {
      var share = unassignedTotal / unassignedParticipants;
      participants.add(
          BillSplitParticipantDomain.builder()
              .name("Participant %d".formatted(namedCount + i + 1))
              .items(List.of())
              .subtotal(share)
              .build());
    }

    response.setParticipants(participants);
  }
}
