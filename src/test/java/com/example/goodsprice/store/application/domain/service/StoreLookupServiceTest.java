package com.example.goodsprice.store.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StoreLookupServiceTest {

  @Mock private StoreRepositoryPort storeRepository;

  @InjectMocks private StoreLookupService storeLookupService;

  @Test
  @DisplayName("Should return empty list when name is null")
  void shouldReturnEmptyListWhenNameIsNull() {
    var result = storeLookupService.findStoreIdsByName(null);

    assertEquals(List.of(), result);
  }

  @Test
  @DisplayName("Should return empty list when name is blank")
  void shouldReturnEmptyListWhenNameIsBlank() {
    var result = storeLookupService.findStoreIdsByName("  ");

    assertEquals(List.of(), result);
  }

  @Test
  @DisplayName("Should return store IDs for valid name")
  void shouldReturnStoreIdsForValidName() {
    var store1 = StoreDomain.builder().id(1L).name("Toko Segar").build();
    var store2 = StoreDomain.builder().id(2L).name("Toko Segar").build();
    when(storeRepository.findByName("Toko Segar")).thenReturn(List.of(store1, store2));

    var result = storeLookupService.findStoreIdsByName("Toko Segar");

    assertEquals(List.of(1L, 2L), result);
    verify(storeRepository).findByName("Toko Segar");
  }

  @Test
  @DisplayName("Should filter out null IDs")
  void shouldFilterOutNullIds() {
    var store1 = StoreDomain.builder().id(1L).name("Toko ABC").build();
    var store2 = StoreDomain.builder().id(null).name("Store Null ID").build();
    when(storeRepository.findByName("Toko ABC")).thenReturn(List.of(store1, store2));

    var result = storeLookupService.findStoreIdsByName("Toko ABC");

    assertEquals(List.of(1L), result);
  }

  @Test
  @DisplayName("Should return empty list when no stores match")
  void shouldReturnEmptyListWhenNoStoresMatch() {
    when(storeRepository.findByName("NonExistent")).thenReturn(List.of());

    var result = storeLookupService.findStoreIdsByName("NonExistent");

    assertEquals(List.of(), result);
  }
}
