package com.example.goodsprice.alert.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;
import com.example.goodsprice.alert.application.port.out.AlertRepositoryPort;
import com.example.goodsprice.common.service.AbstractGenericServiceTest;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest extends AbstractGenericServiceTest {

  @Mock private ProductInPort productInPort;

  @Mock private PriceInPort priceInPort;

  @Mock private AlertRepositoryPort alertRepository;

  @InjectMocks private AlertService alertService;

  @Captor private ArgumentCaptor<AlertSubscription> subscriptionCaptor;

  private ProductDomain product;
  private PriceDomain cheapestPrice;
  private AlertSubscription existingSubscription;

  @Override
  protected Object getService() {
    return alertService;
  }

  @Override
  protected Object getExistingId() {
    return "SUB001";
  }

  @Override
  protected Object getNonExistentId() {
    return "NONEXISTENT";
  }

  @Override
  protected Object getExistingEntity() {
    return existingSubscription;
  }

  @Override
  protected String getNotFoundErrorCode() {
    return "ALERT_NOT_FOUND";
  }

  @Override
  protected void mockFindByIdReturnsEntity() {
    when(alertRepository.findById("SUB001")).thenReturn(existingSubscription);
  }

  @Override
  protected void mockFindByIdReturnsNull() {
    when(alertRepository.findById("NONEXISTENT")).thenReturn(null);
  }

  @Override
  protected void mockDeleteByIdSucceeds() {
    when(alertRepository.findById("SUB001")).thenReturn(existingSubscription);
  }

  @Override
  protected Object invokeFindById(Object id) {
    return alertService.findById((String) id);
  }

  @Override
  protected void invokeDeleteById(Object id) {
    alertService.deleteById((String) id);
  }

  @Override
  protected void verifyDeleteByIdPerformed(Object id) {
    verify(alertRepository).deleteById((String) id);
  }

  @Override
  protected void verifyDeleteByIdNotPerformed() {
    verify(alertRepository, never()).deleteById(any());
  }

  @BeforeEach
  void setUp() {
    product = ProductDomain.builder().id(1L).name("Apple").category("Fruit").build();

    cheapestPrice =
        PriceDomain.builder().id(100L).productId(1L).storeId(10L).price(15_000.0).build();

    existingSubscription =
        AlertSubscription.builder()
            .id("SUB001")
            .productId(1L)
            .productName("Apple")
            .targetPrice(12_000.0)
            .currentPrice(15_000.0)
            .notificationMethod("EMAIL")
            .email("user@test.com")
            .status("ACTIVE")
            .build();
  }

  @Test
  @DisplayName("Should create a subscription with current price")
  void shouldCreateSubscription() {
    when(productInPort.findById(1L)).thenReturn(product);
    when(priceInPort.findCheapestByProduct(1L)).thenReturn(cheapestPrice);
    when(alertRepository.save(any(AlertSubscription.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var domain =
        AlertSubscription.builder()
            .productId(1L)
            .targetPrice(12_000.0)
            .notificationMethod("EMAIL")
            .email("user@test.com")
            .build();
    var result = alertService.subscribe(domain);

    assertNotNull(result);
    assertEquals(1L, result.getProductId());
    assertEquals("Apple", result.getProductName());
    assertEquals(12_000.0, result.getTargetPrice(), 0.001);
    assertEquals(15_000.0, result.getCurrentPrice(), 0.001);
    assertEquals("EMAIL", result.getNotificationMethod());
    assertEquals("user@test.com", result.getEmail());
    assertEquals("ACTIVE", result.getStatus());
    verify(productInPort).findById(1L);
    verify(priceInPort).findCheapestByProduct(1L);
    verify(alertRepository).save(subscriptionCaptor.capture());
    var captured = subscriptionCaptor.getValue();
    assertEquals(1L, captured.getProductId());
    assertEquals("Apple", captured.getProductName());
    assertEquals(15_000.0, captured.getCurrentPrice(), 0.001);
  }

  @Test
  @DisplayName("Should create a subscription with null current price when no price found")
  void shouldCreateSubscriptionWithNullCurrentPrice() {
    when(productInPort.findById(1L)).thenReturn(product);
    when(priceInPort.findCheapestByProduct(1L)).thenReturn(null);
    when(alertRepository.save(any(AlertSubscription.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var domain =
        AlertSubscription.builder()
            .productId(1L)
            .targetPrice(12_000.0)
            .notificationMethod("EMAIL")
            .email("user@test.com")
            .build();
    var result = alertService.subscribe(domain);

    assertNotNull(result);
    assertEquals(1L, result.getProductId());
    assertEquals("Apple", result.getProductName());
    assertEquals(12_000.0, result.getTargetPrice(), 0.001);
    assertNull(result.getCurrentPrice());
    assertEquals("EMAIL", result.getNotificationMethod());
    assertEquals("user@test.com", result.getEmail());
    assertEquals("ACTIVE", result.getStatus());
    verify(productInPort).findById(1L);
    verify(priceInPort).findCheapestByProduct(1L);
    verify(alertRepository).save(any(AlertSubscription.class));
  }

  @Test
  @DisplayName("Should create subscription with SMS notification method")
  void shouldCreateSubscriptionWithSms() {
    when(productInPort.findById(1L)).thenReturn(product);
    when(priceInPort.findCheapestByProduct(1L)).thenReturn(cheapestPrice);
    when(alertRepository.save(any(AlertSubscription.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var domain =
        AlertSubscription.builder()
            .productId(1L)
            .targetPrice(10_000.0)
            .notificationMethod("SMS")
            .email("08123456789")
            .build();
    var result = alertService.subscribe(domain);

    assertNotNull(result);
    assertEquals("SMS", result.getNotificationMethod());
    assertEquals("08123456789", result.getEmail());
    verify(alertRepository).save(subscriptionCaptor.capture());
    assertEquals("SMS", subscriptionCaptor.getValue().getNotificationMethod());
    assertEquals("08123456789", subscriptionCaptor.getValue().getEmail());
  }

  @Test
  @DisplayName("Should throw NullPointerException when product not found")
  void shouldThrowExceptionForInvalidProduct() {
    when(productInPort.findById(999L)).thenThrow(new NullPointerException("Product not found"));

    var domain =
        AlertSubscription.builder()
            .productId(999L)
            .targetPrice(12_000.0)
            .notificationMethod("EMAIL")
            .email("user@test.com")
            .build();
    assertThrows(NullPointerException.class, () -> alertService.subscribe(domain));
  }
}
