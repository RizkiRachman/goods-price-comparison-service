package com.example.goodsprice.alert.application.domain.service;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;
import com.example.goodsprice.alert.application.port.in.AlertInPort;
import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService implements AlertInPort {

  private final ProductInPort productInPort;
  private final PriceInPort priceInPort;

  @Override
  @Transactional
  public AlertSubscription subscribe(
      Long productId, Double targetPrice, String notificationMethod, String email) {
    var product = productInPort.findById(productId);
    var cheapestPrice = priceInPort.findCheapestByProduct(productId);
    var currentPrice = Objects.nonNull(cheapestPrice) ? cheapestPrice.getPrice() : null;

    var subscription =
        AlertSubscription.builder()
            .id(UUID.randomUUID().toString())
            .productId(productId)
            .productName(product.getName())
            .targetPrice(targetPrice)
            .currentPrice(currentPrice)
            .notificationMethod(notificationMethod)
            .email(email)
            .status("ACTIVE")
            .build();

    log.info(
        "Alert subscription created: product={}, targetPrice={}, method={}",
        productId,
        targetPrice,
        notificationMethod);
    return subscription;
  }
}
