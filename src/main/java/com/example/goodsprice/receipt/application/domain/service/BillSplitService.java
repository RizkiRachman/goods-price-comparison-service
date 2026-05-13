package com.example.goodsprice.receipt.application.domain.service;

import com.example.goodsprice.api.model.BillSplitItem;
import com.example.goodsprice.api.model.BillSplitOrder;
import com.example.goodsprice.api.model.BillSplitParticipant;
import com.example.goodsprice.api.model.BillSplitRequest;
import com.example.goodsprice.api.model.BillSplitResponse;
import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.receipt.application.port.in.BillSplitInPort;
import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptItemRepositoryPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
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
  private final ProductInPort productInPort;

  @Override
  @Cacheable(value = "bill-splits", key = "#receiptId + '-' + #request.hashCode()")
  public BillSplitResponse splitBill(UUID receiptId, BillSplitRequest request) {
    var receipt = receiptInPort.findById(receiptId);
    if (Objects.isNull(receipt)) {
      throw new NotFoundException(
          ErrorCodes.RECEIPT_NOT_FOUND, "Receipt not found with id: %s".formatted(receiptId));
    }

    var totalAmount =
        Objects.nonNull(receipt.getTotalAmount()) ? receipt.getTotalAmount().doubleValue() : 0.0;
    var response =
        new BillSplitResponse()
            .receiptId(receiptId)
            .type(
                com.example.goodsprice.api.model.BillSplitResponse.TypeEnum.fromValue(
                    request.getType().getValue()))
            .numberOfParticipants(request.getNumberOfParticipants())
            .totalAmount(totalAmount);

    if ("RATIO".equals(request.getType().getValue())) {
      handleRatioSplit(response, request);
    } else {
      handleSelectionSplit(response, request);
    }

    return response;
  }

  private void handleRatioSplit(BillSplitResponse response, BillSplitRequest request) {
    var totalAmount = response.getTotalAmount();
    var count = request.getNumberOfParticipants();
    var sharePerPerson = count > 0 ? totalAmount / count : 0.0;

    var participants = new ArrayList<BillSplitParticipant>();
    for (int i = 1; i <= count; i++) {
      participants.add(
          new BillSplitParticipant()
              .name("Participant %d".formatted(i))
              .items(List.of())
              .subtotal(sharePerPerson));
    }
    response.setParticipants(participants);
    response.setUnassignedTotal(0.0);
  }

  private void handleSelectionSplit(BillSplitResponse response, BillSplitRequest request) {
    var receiptId = response.getReceiptId();
    var receiptItems = receiptItemRepository.findByReceiptId(receiptId);

    var orders =
        Objects.nonNull(request.getOrders()) ? request.getOrders() : List.<BillSplitOrder>of();

    var ordersByName = orders.stream().collect(Collectors.groupingBy(BillSplitOrder::getName));

    var participants = new ArrayList<BillSplitParticipant>();
    double totalAssigned = 0.0;

    for (var entry : ordersByName.entrySet()) {
      var participantItems = new ArrayList<BillSplitItem>();
      double participantSubtotal = 0.0;

      for (var order : entry.getValue()) {
        var product = productInPort.findById(order.getProductId());
        if (Objects.isNull(product)) continue;

        var matchedItem =
            receiptItems.stream()
                .filter(i -> Objects.equals(i.getProductName(), product.getName()))
                .findFirst()
                .orElse(null);
        if (Objects.isNull(matchedItem)) continue;

        var qty = Objects.nonNull(order.getQuantity()) ? order.getQuantity() : 0.0;
        var unitPrice =
            Objects.nonNull(matchedItem.getUnitPrice()) ? matchedItem.getUnitPrice() : 0.0;
        var subtotal = qty * unitPrice;

        participantItems.add(
            new BillSplitItem()
                .productId(order.getProductId())
                .productName(product.getName())
                .quantity(qty)
                .unitPrice(unitPrice)
                .subtotal(subtotal));
        participantSubtotal += subtotal;
      }

      totalAssigned += participantSubtotal;
      participants.add(
          new BillSplitParticipant()
              .name(entry.getKey())
              .items(participantItems)
              .subtotal(participantSubtotal));
    }

    var namedCount = ordersByName.size();
    var participantCount = request.getNumberOfParticipants();
    var unassignedTotal = Math.max(0.0, response.getTotalAmount() - totalAssigned);
    response.setUnassignedTotal(unassignedTotal);

    var unassignedParticipants = participantCount - namedCount;
    for (int i = 0; i < unassignedParticipants; i++) {
      var share = unassignedParticipants > 0 ? unassignedTotal / unassignedParticipants : 0.0;
      participants.add(
          new BillSplitParticipant()
              .name("Participant %d".formatted(namedCount + i + 1))
              .items(List.of())
              .subtotal(share));
    }

    response.setParticipants(participants);
  }
}
