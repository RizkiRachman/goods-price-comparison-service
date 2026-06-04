package com.example.goodsprice.store.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.CreateStoreRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Store;
import com.example.goodsprice.api.model.UpdateStoreRequest;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.in.StoreInPort;
import com.example.goodsprice.store.application.port.in.dto.CreateStoreCriteria;
import com.example.goodsprice.store.application.port.in.dto.StoreCriteria;
import com.example.goodsprice.store.application.port.in.dto.UpdateStoreCriteria;
import com.example.goodsprice.store.infrastructure.adapter.web.mapper.StoreDtoMapper;
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
class StoreWebAdapterTest {

  @Mock private StoreInPort storeInPort;
  @Mock private StoreDtoMapper mapper;

  @InjectMocks private StoreWebAdapter storeWebAdapter;

  @Captor private ArgumentCaptor<CreateStoreCriteria> createCaptor;
  @Captor private ArgumentCaptor<UpdateStoreCriteria> updateCaptor;

  private StoreDomain storeDomain;
  private Store storeApi;

  @BeforeEach
  void setUp() {
    storeDomain =
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

    storeApi = new Store();
    storeApi.setId(1L);
    storeApi.setName("Toko Segar");
  }

  @Test
  @DisplayName("Should create store from request")
  void shouldCreateStore() {
    var request = new CreateStoreRequest();
    request.setName("Toko Segar");
    request.setLocation("Jakarta");
    request.setChain("Segar Group");
    request.setAddress("Jl. Sudirman No. 1");
    request.setLatitude(-6.2);
    request.setLongitude(106.8);

    when(storeInPort.create(any(CreateStoreCriteria.class))).thenReturn(storeDomain);
    when(mapper.toApiStore(storeDomain)).thenReturn(storeApi);

    var result = storeWebAdapter.create(request);

    assertNotNull(result);
    assertEquals("Toko Segar", result.getName());
    verify(storeInPort).create(any(CreateStoreCriteria.class));
  }

  @Test
  @DisplayName("Should get store by id")
  void shouldGetStoreById() {
    when(storeInPort.findById(1L)).thenReturn(storeDomain);
    when(mapper.toApiStore(storeDomain)).thenReturn(storeApi);

    var result = storeWebAdapter.getById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
  }

  @Test
  @DisplayName("Should throw NotFoundException when store not found")
  void shouldThrowNotFoundExceptionWhenStoreNotFound() {
    when(storeInPort.findById(999L)).thenThrow(NotFoundException.store(999L));

    assertThrows(NotFoundException.class, () -> storeWebAdapter.getById(999L));
  }

  @Test
  @DisplayName("Should list stores with pagination")
  void shouldListStores() {
    var pageResponse = PageResponse.of(List.of(storeDomain), 1, 20, 1);
    when(storeInPort.findAll(any(StoreCriteria.class))).thenReturn(pageResponse);
    when(mapper.toApiStore(storeDomain)).thenReturn(storeApi);

    var result =
        storeWebAdapter.list(1, 20, "name", "asc", "search", EntityStatus.APPROVED, "chain", "loc");

    assertNotNull(result);
    assertEquals(1, result.getData().size());
    assertEquals(1, result.getPagination().getTotalItems());
  }

  @Test
  @DisplayName("Should list stores with default pagination when null")
  void shouldListStoresWithDefaults() {
    var pageResponse = PageResponse.of(List.of(storeDomain), 0, 20, 1);
    when(storeInPort.findAll(any(StoreCriteria.class))).thenReturn(pageResponse);
    when(mapper.toApiStore(storeDomain)).thenReturn(storeApi);

    var result = storeWebAdapter.list(null, null, null, null, null, null, null, null);

    assertNotNull(result);
    assertEquals(1, result.getData().size());
  }

  @Test
  @DisplayName("Should update store")
  void shouldUpdateStore() {
    var request = new UpdateStoreRequest();
    request.setName("Toko Segar Updated");
    request.setLocation("Jakarta Pusat");

    var updatedDomain =
        StoreDomain.builder().id(1L).name("Toko Segar Updated").location("Jakarta Pusat").build();

    var updatedApi = new Store();
    updatedApi.setId(1L);
    updatedApi.setName("Toko Segar Updated");

    when(storeInPort.update(any(UpdateStoreCriteria.class))).thenReturn(updatedDomain);
    when(mapper.toApiStore(updatedDomain)).thenReturn(updatedApi);

    var result = storeWebAdapter.update(1L, request);

    assertNotNull(result);
    assertEquals("Toko Segar Updated", result.getName());
    verify(storeInPort).update(any(UpdateStoreCriteria.class));
  }

  @Test
  @DisplayName("Should delete store")
  void shouldDeleteStore() {
    storeWebAdapter.delete(1L);

    verify(storeInPort).deleteById(1L);
  }
}
