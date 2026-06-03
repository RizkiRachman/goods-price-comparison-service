package com.example.goodsprice.product.application.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.goodsprice.product.application.domain.model.ProductDomain;
import java.util.Comparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductComparatorsTest {

  @InjectMocks private ProductComparators productComparators;

  @Mock
  private ProductDomain
      productDomain; // Mock for potential future use or if ProductDomain had methods called by

  // comparators directly

  @BeforeEach
  void setUp() {
    // ProductComparators has no dependencies injected via constructor or setters,
    // so @InjectMocks is sufficient. The comparators map is initialized in the constructor.
    // If ProductComparators had dependencies, they would be mocked here.
  }

  @Test
  @DisplayName("Resolve comparator for null sortBy returns null")
  void resolve_nullSortBy_returnsNull() {
    assertNull(productComparators.resolve(null), "Should return null for null sortBy");
  }

  @Test
  @DisplayName("Resolve comparator for empty sortBy returns null")
  void resolve_emptySortBy_returnsNull() {
    assertNull(productComparators.resolve(""), "Should return null for empty sortBy");
  }

  @Test
  @DisplayName("Resolve comparator for valid sortBy returns correct comparator")
  void resolve_validSortBy_returnsCorrectComparator() {
    // Test for "name" comparator
    Comparator<ProductDomain> nameComparator = productComparators.resolve("name");
    assertNotNull(nameComparator, "Comparator for 'name' should not be null");
    // Example assertion: create two products and check their order
    ProductDomain p1 = ProductDomain.builder().id(1L).name("Apple").build();
    ProductDomain p2 = ProductDomain.builder().name("Banana").build();
    assertTrue(
        nameComparator.compare(p1, p2) < 0, "Comparator should sort 'Apple' before 'Banana'");

    // Test for "id" comparator
    Comparator<ProductDomain> idComparator = productComparators.resolve("id");
    assertNotNull(idComparator, "Comparator for 'id' should not be null");
    ProductDomain p3 = ProductDomain.builder().id(1L).build();
    ProductDomain p4 = ProductDomain.builder().id(2L).build();
    assertTrue(idComparator.compare(p3, p4) < 0, "Comparator should sort ID 1 before ID 2");

    // Test for case-insensitivity
    Comparator<ProductDomain> nameComparatorCaseInsensitive = productComparators.resolve("NAME");
    assertNotNull(
        nameComparatorCaseInsensitive, "Comparator should be case-insensitive for 'NAME'");
    assertTrue(
        nameComparatorCaseInsensitive.compare(p1, p2) < 0,
        "Comparator should sort 'Apple' before 'Banana' (case-insensitive)");
  }

  @Test
  @DisplayName("Resolve comparator for invalid sortBy returns null")
  void resolve_invalidSortBy_returnsNull() {
    assertNull(
        productComparators.resolve("invalidField"),
        "Should return null for an invalid sortBy field");
  }

  @Test
  @DisplayName("Has comparator for null sortBy returns false")
  void hasComparator_nullSortBy_returnsFalse() {
    assertFalse(productComparators.hasComparator(null), "Should return false for null sortBy");
  }

  @Test
  @DisplayName("Has comparator for empty sortBy returns false")
  void hasComparator_emptySortBy_returnsFalse() {
    assertFalse(productComparators.hasComparator(""), "Should return false for empty sortBy");
  }

  @Test
  @DisplayName("Has comparator for valid sortBy returns true")
  void hasComparator_validSortBy_returnsTrue() {
    assertTrue(
        productComparators.hasComparator("name"), "Should return true for valid sortBy 'name'");
    assertTrue(productComparators.hasComparator("id"), "Should return true for valid sortBy 'id'");
    assertTrue(
        productComparators.hasComparator("status"), "Should return true for valid sortBy 'status'");
    // Test case-insensitivity
    assertTrue(
        productComparators.hasComparator("CATEGORY"),
        "Should return true for valid sortBy 'CATEGORY' (case-insensitive)");
  }

  @Test
  @DisplayName("Has comparator for invalid sortBy returns false")
  void hasComparator_invalidSortBy_returnsFalse() {
    assertFalse(
        productComparators.hasComparator("nonExistentField"),
        "Should return false for an invalid sortBy field");
  }

  @Test
  @DisplayName("Resolve comparator for a valid sortBy field should return a non-null comparator")
  void resolve_validField_returnsComparator() {
    Comparator<ProductDomain> comparator = productComparators.resolve("name");
    assertNotNull(comparator);
  }

  @Test
  @DisplayName("Resolve comparator for an invalid sortBy field should return null")
  void resolve_invalidField_returnsNull() {
    Comparator<ProductDomain> comparator =
        productComparators.resolve("price"); // Assuming 'price' is not a valid sort field
    assertNull(comparator);
  }

  @Test
  @DisplayName("Has comparator for a valid sortBy field should return true")
  void hasComparator_validField_returnsTrue() {
    assertTrue(productComparators.hasComparator("brand")); // Assuming 'brand' is a valid sort field
  }

  @Test
  @DisplayName("Has comparator for an invalid sortBy field should return false")
  void hasComparator_invalidField_returnsFalse() {
    assertFalse(
        productComparators.hasComparator(
            "discount")); // Assuming 'discount' is not a valid sort field
  }
}
