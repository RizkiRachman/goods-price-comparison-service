package com.example.goodsprice.product.application.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import com.example.goodsprice.common.service.AbstractGenericService;
import com.example.goodsprice.common.service.AbstractGenericServiceTest;
import com.example.goodsprice.common.service.ServiceLayerNotFoundExceptionTest;
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
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest extends AbstractGenericServiceTest
    implements ServiceLayerNotFoundExceptionTest {

  @Mock private ProductRepositoryPort productRepository;
  @Mock private ProductPriceQueryInPort productPriceQueryInPort;
  @Mock private StoreLookupInPort storeLookupInPort;

  @InjectMocks private ProductService productService;

  private ProductDomain product;

  @Override
  protected AbstractGenericService getService() {
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
  public String getNotFoundErrorCode() {
    return "PRODUCT_NOT_FOUND";
  }

  @Override
  public void mockRepositoryReturnsNull() {
    when(productRepository.findById(999L)).thenReturn(null);
  }

  @Override
  public Executable serviceMethodThatShouldThrowNotFound() {
    var input = ProductDomain.builder().name("x").category("x").brand("x").unit("x").build();
    return () -> productService.update(999L, input);
  }

  @Override
  protected GenericRepositoryPort getRepository() {
    return productRepository;
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

  @Test
  void shouldReturnEmptyMapWhenCreateIfNotExistBatchWithNullItems() {
    var result = productService.createIfNotExistBatch(null);

    assertTrue(result.isEmpty());
    verify(productRepository, never()).findAllByNames(any());
    verify(productRepository, never()).save(any());
  }

  @Test
  void shouldReturnEmptyMapWhenCreateIfNotExistBatchWithEmptyList() {
    var result = productService.createIfNotExistBatch(List.of());

    assertTrue(result.isEmpty());
    verify(productRepository, never()).findAllByNames(any());
    verify(productRepository, never()).save(any());
  }

  @Test
  void shouldSearchWithNumericStoreId() {
    var criteria =
        ProductSearchCriteria.builder().search("Susu").storeId("1").page(0).size(20).build();

    when(productPriceQueryInPort.findProductIdsByStoreIds(List.of(1L)))
        .thenReturn(List.of(10L, 20L));
    var pageResponse = PageResponse.of(List.of(product), 0, 20, 1);
    when(productRepository.search(any())).thenReturn(pageResponse);

    var result = productService.search(criteria);

    assertEquals(1, result.content().size());
    assertEquals("Susu Kotak", result.content().getFirst().getName());
    verify(productPriceQueryInPort).findProductIdsByStoreIds(List.of(1L));
    verify(productRepository).search(any());
  }

  @Test
  void shouldSearchWithStoreName() {
    var criteria =
        ProductSearchCriteria.builder().search("Susu").storeId("Toko A").page(0).size(20).build();

    when(storeLookupInPort.findStoreIdsByName("Toko A")).thenReturn(List.of(1L, 2L));
    when(productPriceQueryInPort.findProductIdsByStoreIds(List.of(1L, 2L)))
        .thenReturn(List.of(10L, 20L));
    var pageResponse = PageResponse.of(List.of(product), 0, 20, 1);
    when(productRepository.search(any())).thenReturn(pageResponse);

    var result = productService.search(criteria);

    assertEquals(1, result.content().size());
    verify(storeLookupInPort).findStoreIdsByName("Toko A");
    verify(productPriceQueryInPort).findProductIdsByStoreIds(List.of(1L, 2L));
  }

  @Test
  void shouldReturnEmptyWhenStoreNameNotFound() {
    var criteria = ProductSearchCriteria.builder().storeId("Toko A").page(0).size(20).build();

    when(storeLookupInPort.findStoreIdsByName("Toko A")).thenReturn(List.of());

    var result = productService.search(criteria);

    assertTrue(result.content().isEmpty());
    verify(productRepository, never()).search(any());
  }

  @Test
  void shouldReturnEmptyWhenNoProductsAtStore() {
    var criteria = ProductSearchCriteria.builder().storeId("1").page(0).size(20).build();

    when(productPriceQueryInPort.findProductIdsByStoreIds(List.of(1L))).thenReturn(List.of());

    var result = productService.search(criteria);

    assertTrue(result.content().isEmpty());
    verify(productRepository, never()).search(any());
  }

  @Test
  void shouldReturnEmptyWhenStoreLookupReturnsNull() {
    var criteria = ProductSearchCriteria.builder().storeId("Toko A").page(0).size(20).build();

    when(storeLookupInPort.findStoreIdsByName("Toko A")).thenReturn(null);

    var result = productService.search(criteria);

    assertTrue(result.content().isEmpty());
    verify(productRepository, never()).search(any());
  }

  @Test
  void shouldReturnEmptyWhenProductIdsAreNull() {
    var criteria = ProductSearchCriteria.builder().storeId("1").page(0).size(20).build();

    when(productPriceQueryInPort.findProductIdsByStoreIds(List.of(1L))).thenReturn(null);

    var result = productService.search(criteria);

    assertTrue(result.content().isEmpty());
    verify(productRepository, never()).search(any());
  }

  @Test
  void shouldCreateIfNotExistBatchWithMixedExistingAndNew() {
    var existingProduct =
        ProductDomain.builder()
            .id(10L)
            .name("Apple")
            .category("Fruit")
            .unit("KG")
            .status("ACTIVE")
            .build();

    var item1 = new ProductInPort.ProductCreateItem("Apple", "Fruit", "KG");
    var item2 = new ProductInPort.ProductCreateItem("Banana", "Fruit", "KG");

    when(productRepository.findAllByNames(any())).thenReturn(List.of(existingProduct));
    when(productRepository.save(any(ProductDomain.class)))
        .thenAnswer(
            inv -> {
              var p = inv.<ProductDomain>getArgument(0);
              if (p.getId() == null) p.setId(2L);
              return p;
            });

    var result = productService.createIfNotExistBatch(List.of(item1, item2));

    assertThat(result).hasSize(2);
    assertThat(result).containsKey("Apple");
    assertThat(result).containsKey("Banana");
    verify(productRepository, times(1)).save(any(ProductDomain.class));
    verify(productRepository).findAllByNames(any());
  }
}
