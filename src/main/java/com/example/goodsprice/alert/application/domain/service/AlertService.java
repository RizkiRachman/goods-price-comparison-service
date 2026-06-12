package com.example.goodsprice.alert.application.domain.service;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;
import com.example.goodsprice.alert.application.port.in.AlertInPort;
import com.example.goodsprice.alert.application.port.out.AlertRepositoryPort;
import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import com.example.goodsprice.common.service.AbstractGenericService;
import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AlertService extends AbstractGenericService<AlertSubscription, String>
    implements AlertInPort {

  private final ProductInPort productInPort;
  private final PriceInPort priceInPort;
  private final AlertRepositoryPort alertRepository;

  public AlertService(
      ProductInPort productInPort, PriceInPort priceInPort, AlertRepositoryPort alertRepository) {
    super("AlertSubscription", ErrorCodes.ALERT_NOT_FOUND);
    this.productInPort = productInPort;
    this.priceInPort = priceInPort;
    this.alertRepository = alertRepository;
  }

  @Override
  protected GenericRepositoryPort<AlertSubscription, String> getRepository() {
    return alertRepository;
  }

  @Override
  @Transactional
  public AlertSubscription subscribe(AlertSubscription domain) {
    var productId = domain.getProductId();
    var product = productInPort.findById(productId);
    var cheapestPrice = priceInPort.findCheapestByProduct(productId);
    var currentPrice = Objects.nonNull(cheapestPrice) ? cheapestPrice.getPrice() : null;

    var subscription =
        AlertSubscription.builder()
            .id(UUID.randomUUID().toString())
            .productId(productId)
            .productName(product.getName())
            .targetPrice(domain.getTargetPrice())
            .currentPrice(currentPrice)
            .notificationMethod(domain.getNotificationMethod())
            .email(domain.getEmail())
            .status("ACTIVE")
            .build();

    var saved = save(subscription);

    log.info(
        "Alert subscription created: product={}, targetPrice={}, method={}",
        productId,
        domain.getTargetPrice(),
        domain.getNotificationMethod());
    return saved;
  }
}
