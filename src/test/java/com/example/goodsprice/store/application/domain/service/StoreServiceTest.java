package com.example.goodsprice.store.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.in.dto.StoreCriteria;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import java.util.List;
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
class StoreServiceTest {

  @Mock private StoreRepositoryPort storeRepository;

  @InjectMocks private StoreService storeService;

  @Captor private ArgumentCaptor<StoreDomain> storeCaptor;

  private StoreDomain store1;
  private StoreDomain store2;

  @BeforeEach
  void setUp() {
    store1 =
        StoreDomain.builder()
            .id(1L)
            .name("Toko Segar")
            .location("Jakarta")
            .chain("Segar Group")
            .address("Jl. Sudirman No. 1")
            .latitude(-6.2)
            .longitude(106.8)
            .status("ACTIVE")
            .build();
    store2 =
        StoreDomain.builder()
            .id(2L)
            .name("Toko Makmur")
            .location("Bandung")
            .chain("Makmur Group")
            .address("Jl. Asia Afrika No. 10")
            .latitude(-6.9)
            .longitude(107.6)
            .status("ACTIVE")
            .build();
  }

  @Test
  @DisplayName("Should create a new store")
  void shouldCreateStore() {
    when(storeRepository.save(any(StoreDomain.class))).thenReturn(store1);

    var result =
        storeService.create(
            "Toko Segar", "Jakarta", "Segar Group", "Jl. Sudirman No. 1", -6.2, 106.8);

    assertNotNull(result);
    assertEquals("Toko Segar", result.getName());
    assertEquals("Jakarta", result.getLocation());
    assertEquals("Segar Group", result.getChain());
    assertEquals("Jl. Sudirman No. 1", result.getAddress());
    assertEquals(-6.2, result.getLatitude(), 0.001);
    assertEquals(106.8, result.getLongitude(), 0.001);
    verify(storeRepository).save(any(StoreDomain.class));
  }

  @Test
  @DisplayName("Should find store by id")
  void shouldFindStoreById() {
    when(storeRepository.findById(1L)).thenReturn(store1);

    var result = storeService.findById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("Toko Segar", result.getName());
    verify(storeRepository).findById(1L);
  }

  @Test
  @DisplayName("Should throw NotFoundException when store not found")
  void shouldThrowExceptionWhenStoreNotFound() {
    when(storeRepository.findById(999L)).thenReturn(null);

    var exception = assertThrows(NotFoundException.class, () -> storeService.findById(999L));
    assertEquals("STORE_NOT_FOUND", exception.getErrorCode());
  }

  @Test
  @DisplayName("Should update an existing store")
  void shouldUpdateExistingStore() {
    when(storeRepository.findById(1L)).thenReturn(store1);
    when(storeRepository.save(any(StoreDomain.class))).thenReturn(store1);

    var result =
        storeService.update(
            1L,
            "Toko Segar Baru",
            "Jakarta Pusat",
            "Segar Group",
            "Jl. Sudirman No. 5",
            -6.3,
            106.9,
            "ACTIVE");

    assertEquals("Toko Segar Baru", result.getName());
    assertEquals("Jakarta Pusat", result.getLocation());
    assertEquals("Jl. Sudirman No. 5", result.getAddress());
    assertEquals(-6.3, result.getLatitude(), 0.001);
    assertEquals(106.9, result.getLongitude(), 0.001);
    verify(storeRepository).save(storeCaptor.capture());
    var saved = storeCaptor.getValue();
    assertEquals("Toko Segar Baru", saved.getName());
    assertEquals("Jakarta Pusat", saved.getLocation());
  }

  @Test
  @DisplayName("Should delete an existing store")
  void shouldDeleteExistingStore() {
    when(storeRepository.findById(1L)).thenReturn(store1);

    storeService.deleteById(1L);

    verify(storeRepository).findById(1L);
    verify(storeRepository).deleteById(1L);
  }

  @Test
  @DisplayName("Should throw NotFoundException when deleting non-existent store")
  void shouldThrowExceptionWhenDeletingNonExistentStore() {
    when(storeRepository.findById(999L)).thenReturn(null);

    assertThrows(NotFoundException.class, () -> storeService.deleteById(999L));
    verify(storeRepository).findById(999L);
  }

  @Test
  @DisplayName("Should find all stores with pagination and filters")
  void shouldFindAllStores() {
    var stores = List.of(store1, store2);
    var pageResponse = PageResponse.of(stores, 0, 10, stores.size());
    var pageRequest = new PageRequestDto(0, 10, "name", "asc");
    var criteria = new StoreCriteria(pageRequest, null, "ACTIVE", "Segar Group", "Jakarta");
    when(storeRepository.findAll(any(StoreCriteria.class))).thenReturn(pageResponse);

    var result = storeService.findAll(criteria);

    assertNotNull(result);
    assertEquals(2, result.totalElements());
    assertEquals("Toko Segar", result.content().get(0).getName());
    verify(storeRepository).findAll(any(StoreCriteria.class));
  }

  @Test
  @DisplayName("Should find all stores by list of ids")
  void shouldFindAllByIds() {
    when(storeRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(store1, store2));

    var results = storeService.findAllById(List.of(1L, 2L));

    assertNotNull(results);
    assertEquals(2, results.size());
    assertEquals("Toko Segar", results.get(0).getName());
    assertEquals("Toko Makmur", results.get(1).getName());
    verify(storeRepository).findAllById(List.of(1L, 2L));
  }
}
