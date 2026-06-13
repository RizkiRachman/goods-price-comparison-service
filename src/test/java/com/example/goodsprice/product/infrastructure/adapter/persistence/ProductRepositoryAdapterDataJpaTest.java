package com.example.goodsprice.product.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.goodsprice.common.persistence.AbstractRepositoryAdapterDataJpaTest;
import com.example.goodsprice.product.infrastructure.adapter.persistence.entity.ProductEntity;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProductRepositoryAdapterDataJpaTest extends AbstractRepositoryAdapterDataJpaTest {

  @Autowired private JpaProductRepository repository;

  @Override
  protected Object getRepository() {
    return repository;
  }

  @Test
  @DisplayName("Should persist and retrieve product with all fields")
  void shouldPersistAndRetrieveProduct() {
    var entity = new ProductEntity();
    entity.setName("Susu Kotak");
    entity.setCategory("Minuman");
    entity.setBrand("Indomilk");
    entity.setUnit("KG");
    entity.setStatus("ACTIVE");
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findById(entity.getId());

    assertTrue(found.isPresent());
    var product = found.get();
    assertEquals("Susu Kotak", product.getName());
    assertEquals("Minuman", product.getCategory());
    assertEquals("Indomilk", product.getBrand());
    assertEquals("KG", product.getUnit());
    assertEquals("ACTIVE", product.getStatus());
  }

  @Test
  @DisplayName("Should find product by name")
  void shouldFindByName() {
    var entity = new ProductEntity();
    entity.setName("Unique Product");
    entity.setCategory("Kategori");
    repository.saveAndFlush(entity);
    entityManager.clear();

    Optional<ProductEntity> found = repository.findByName("Unique Product");

    assertTrue(found.isPresent());
    assertEquals("Unique Product", found.get().getName());
  }

  @Test
  @DisplayName("Should return empty when name not found")
  void shouldReturnNullWhenNameNotFound() {
    Optional<ProductEntity> found = repository.findByName("NonExistent Product");

    assertTrue(found.isEmpty());
  }

  @Test
  @DisplayName("Should search products by name containing ignore case")
  void shouldSearchByNameContainingIgnoreCase() {
    var entity1 = new ProductEntity();
    entity1.setName("Susu Kotak");
    var entity2 = new ProductEntity();
    entity2.setName("Susu Bubuk");
    repository.saveAndFlush(entity1);
    repository.saveAndFlush(entity2);
    entityManager.clear();

    List<ProductEntity> results = repository.findByNameContainingIgnoreCase("susu");

    assertEquals(2, results.size());
  }

  @Test
  @DisplayName("Should find all products by names list")
  void shouldFindAllByNames() {
    var entity1 = new ProductEntity();
    entity1.setName("Product A");
    var entity2 = new ProductEntity();
    entity2.setName("Product B");
    repository.saveAndFlush(entity1);
    repository.saveAndFlush(entity2);
    entityManager.clear();

    List<ProductEntity> results =
        repository.findByNameIn(List.of("Product A", "Product B", "NonExistent"));

    assertEquals(2, results.size());
  }

  @Test
  @DisplayName("Should check if product exists by name")
  void shouldCheckExistsByName() {
    var entity = new ProductEntity();
    entity.setName("Existing Product");
    repository.saveAndFlush(entity);
    entityManager.clear();

    assertTrue(repository.existsByName("Existing Product"));
    assertFalse(repository.existsByName("Missing Product"));
  }

  @Test
  @DisplayName("Should find products by category")
  void shouldFindByCategory() {
    var entity1 = new ProductEntity();
    entity1.setName("Product A");
    entity1.setCategory("Minuman");
    var entity2 = new ProductEntity();
    entity2.setName("Product B");
    entity2.setCategory("Minuman");
    var entity3 = new ProductEntity();
    entity3.setName("Product C");
    entity3.setCategory("Makanan");
    repository.saveAndFlush(entity1);
    repository.saveAndFlush(entity2);
    repository.saveAndFlush(entity3);
    entityManager.clear();

    List<ProductEntity> results = repository.findByCategory("Minuman");

    assertEquals(2, results.size());
    assertTrue(results.stream().allMatch(p -> "Minuman".equals(p.getCategory())));
  }

  @Test
  @DisplayName("Should persist all product entity fields")
  void shouldPersistAllProductFields() {
    var entity = new ProductEntity();
    entity.setName("Complete Product");
    entity.setCategory("Kategori");
    entity.setBrand("Merk");
    entity.setUnit("PCS");
    entity.setStatus("ACTIVE");
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findById(entity.getId()).orElseThrow();

    assertEquals("Complete Product", found.getName());
    assertEquals("Kategori", found.getCategory());
    assertEquals("Merk", found.getBrand());
    assertEquals("PCS", found.getUnit());
    assertEquals("ACTIVE", found.getStatus());
    assertNotNull(found.getCreatedAt());
    assertNotNull(found.getUpdatedAt());
  }
}
