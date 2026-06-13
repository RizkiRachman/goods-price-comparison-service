package com.example.goodsprice.product.application.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.common.service.AbstractGenericServiceTest;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.domain.model.ProductSearchCriteria;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.product.application.port.in.ProductPriceQueryInPort;
import com.example.goodsprice.product.application.port.in.StoreLookupInPort;
import com.example.goodsprice.product.application.port.out.ProductRepositoryPort;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest extends AbstractGenericServiceTest {

  @Mock private ProductRepositoryPort productRepository;
  @Mock private ProductPriceQueryInPort productPriceQueryInPort;
  @Mock private StoreLookupInPort storeLookupInPort;

  @InjectMocks private ProductService productService;

  private ProductDomain product;

  @Override
  protected Object getService() {
    return productService;
  }

  @Override
  protected Object getExistingId() {
    return 1L;
  }

  @Override
  protected Object getNonExistentId() {
    return 999L;
  }

  @Override
  protected Object getExistingEntity() {
    return product;
  }

  @Override
  protected String getNotFoundErrorCode() {
    return "PRODUCT_NOT_FOUND";
  }

  @Override
  protected void mockFindByIdReturnsEntity() {
    when(productRepository.findById(1L)).thenReturn(product);
  }

  @Override
  protected void mockFindByIdReturnsNull() {
    when(productRepository.findById(999L)).thenReturn(null);
  }

  @Override
  protected void mockDeleteByIdSucceeds() {
    when(productRepository.findById(1L)).thenReturn(product);
  }

  @Override
  protected Object invokeFindById(Object id) {
    return productService.findById((Long) id);
  }

  @Override
  protected void invokeDeleteById(Object id) {
    productService.deleteById((Long) id);
  }

  @Override
  protected void verifyDeleteByIdPerformed(Object id) {
    verify(productRepository).deleteById((Long) id);
  }

  @Override
  protected void verifyDeleteByIdNotPerformed() {
    verify(productRepository, never()).deleteById(any());
  }

  @BeforeEach
  void setUp() {
    product =
        ProductDomain.builder()
            .id(1L)
            .name("Susu Kotak")
            .category("Minuman")
            .brand("Indomilk")
            .unit("KG")
            .status("ACTIVE")
            .build();
  }

  @Test
  void shouldCreateProduct() {
    when(productRepository.save(any(ProductDomain.class))).thenReturn(product);

    var input =
        ProductDomain.builder()
            .name("Susu Kotak")
            .category("Minuman")
            .brand("Indomilk")
            .unit("KG")
            .build();
    var result = productService.create(input);

    assertNotNull(result);
    assertEquals("Susu Kotak", result.getName());
    verify(productRepository).save(any(ProductDomain.class));
  }

  @Test
  void shouldCreateIfNotExistWhenNotFound() {
    when(productRepository.findByName("Susu Kotak")).thenReturn(null);
    when(productRepository.save(any(ProductDomain.class))).thenReturn(product);

    var result = productService.createIfNotExist("Susu Kotak", "Minuman", "KG");

    assertNotNull(result);
    assertEquals("Susu Kotak", result.getName());
    verify(productRepository).save(any(ProductDomain.class));
  }

  @Test
  void shouldReturnExistingWhenCreateIfNotExist() {
    when(productRepository.findByName("Susu Kotak")).thenReturn(product);

    var result = productService.createIfNotExist("Susu Kotak", "Minuman", "KG");

    assertNotNull(result);
    assertEquals("Susu Kotak", result.getName());
    verify(productRepository, never()).save(any());
  }

  @Test
  void shouldFindByName() {
    when(productRepository.findByName("Susu Kotak")).thenReturn(product);

    var result = productService.findByName("Susu Kotak");

    assertNotNull(result);
    assertEquals("Susu Kotak", result.getName());
  }

  @Test
  void shouldSearchByName() {
    when(productRepository.searchByName("Susu")).thenReturn(List.of(product));

    var result = productService.searchByName("Susu");

    assertEquals(1, result.size());
    assertEquals("Susu Kotak", result.getFirst().getName());
  }

  @Test
  void shouldFindAllByNames() {
    when(productRepository.findAllByNames(List.of("Susu Kotak"))).thenReturn(List.of(product));

    var result = productService.findAllByNames(List.of("Susu Kotak"));

    assertEquals(1, result.size());
  }

  @Test
  void shouldFindAll() {
    when(productRepository.findAll()).thenReturn(List.of(product));

    var result = productService.findAllProducts();

    assertEquals(1, result.size());
  }

  @Test
  void shouldUpdateProduct() {
    when(productRepository.findById(1L)).thenReturn(product);
    when(productRepository.save(any(ProductDomain.class))).thenReturn(product);

    var input =
        ProductDomain.builder()
            .name("Updated")
            .category("Makanan")
            .brand("Baru")
            .unit("LITER")
            .build();
    var result = productService.update(1L, input);

    assertNotNull(result);
    assertEquals("Updated", result.getName());
    verify(productRepository).save(any(ProductDomain.class));
  }

  @Test
  void shouldThrowNotFoundWhenUpdateFails() {
    when(productRepository.findById(999L)).thenReturn(null);

    var input = ProductDomain.builder().name("x").category("x").brand("x").unit("x").build();
    assertThrows(NotFoundException.class, () -> productService.update(999L, input));
  }

  @Test
  void shouldUpdateLastPriceUpdate() {
    var now = LocalDateTime.now();
    productService.updateLastPriceUpdate(1L, now);

    verify(productRepository).updateLastPriceUpdate(1L, now);
  }

  @Test
  void shouldSearchWithCriteria() {
    var criteria = ProductSearchCriteria.builder().search("Susu").page(0).size(20).build();
    var pageResponse = PageResponse.of(List.of(product), 0, 20, 1);
    when(productRepository.search(criteria)).thenReturn(pageResponse);

    var result = productService.search(criteria);

    assertEquals(1, result.content().size());
    assertEquals(1, result.totalElements());
  }

  @Test
  void shouldCreateIfNotExistBatch() {
    var item1 = new ProductInPort.ProductCreateItem("Apple", "Fruit", "KG");
    var item2 = new ProductInPort.ProductCreateItem("Banana", "Fruit", "KG");

    when(productRepository.findAllByNames(any())).thenReturn(List.of());
    when(productRepository.save(any(ProductDomain.class)))
        .thenAnswer(
            inv -> {
              var p = inv.<ProductDomain>getArgument(0);
              if (p.getId() == null) p.setId(1L);
              return p;
            });

    var result = productService.createIfNotExistBatch(List.of(item1, item2));

    assertThat(result).hasSize(2);
    assertThat(result).containsKeys("Apple", "Banana");
    verify(productRepository, times(2)).save(any(ProductDomain.class));
  }
}
