package com.example.goodsprice.unit.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.service.AbstractGenericServiceTest;
import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import com.example.goodsprice.unit.application.domain.model.UnitType;
import com.example.goodsprice.unit.application.port.in.dto.UnitCriteria;
import com.example.goodsprice.unit.application.port.out.UnitRepositoryPort;
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
class UnitServiceTest extends AbstractGenericServiceTest {

  @Mock private UnitRepositoryPort unitRepository;

  @InjectMocks private UnitService unitService;

  @Captor private ArgumentCaptor<UnitDomain> unitCaptor;

  private UnitDomain kgUnit;

  @Override
  protected Object getService() {
    return unitService;
  }

  @Override
  protected Object getExistingId() {
    return "KG";
  }

  @Override
  protected Object getNonExistentId() {
    return "NONEXISTENT";
  }

  @Override
  protected Object getExistingEntity() {
    return kgUnit;
  }

  @Override
  protected String getNotFoundErrorCode() {
    return "UNIT_NOT_FOUND";
  }

  @Override
  protected void mockFindByIdReturnsEntity() {
    when(unitRepository.findById("KG")).thenReturn(kgUnit);
  }

  @Override
  protected void mockFindByIdReturnsNull() {
    when(unitRepository.findById("NONEXISTENT")).thenReturn(null);
  }

  @Override
  protected void mockDeleteByIdSucceeds() {
    when(unitRepository.findById("KG")).thenReturn(kgUnit);
  }

  @Override
  protected Object invokeFindById(Object id) {
    return unitService.findById((String) id);
  }

  @Override
  protected void invokeDeleteById(Object id) {
    unitService.deleteById((String) id);
  }

  @Override
  protected void verifyDeleteByIdPerformed(Object id) {
    verify(unitRepository).deleteById((String) id);
  }

  @Override
  protected void verifyDeleteByIdNotPerformed() {
    verify(unitRepository, never()).deleteById(any());
  }

  @BeforeEach
  void setUp() {
    kgUnit =
        UnitDomain.builder()
            .id("KG")
            .name("Kilogram")
            .symbol("kg")
            .type(UnitType.WEIGHT)
            .description("Unit of mass")
            .status("ACTIVE")
            .build();
  }

  @Test
  @DisplayName("Should create a unit")
  void shouldCreateUnit() {
    when(unitRepository.save(any(UnitDomain.class))).thenReturn(kgUnit);

    var domain =
        UnitDomain.builder()
            .id("KG")
            .name("Kilogram")
            .symbol("kg")
            .type(UnitType.WEIGHT)
            .description("Unit of mass")
            .build();
    var result = unitService.create(domain);

    assertNotNull(result);
    assertEquals("KG", result.getId());
    assertEquals("Kilogram", result.getName());
    assertEquals("kg", result.getSymbol());
    assertEquals(UnitType.WEIGHT, result.getType());
    assertEquals("ACTIVE", result.getStatus());
    verify(unitRepository).save(any(UnitDomain.class));
  }

  @Test
  @DisplayName("Should find all units with type filter")
  void shouldFindAllUnitsWithTypeFilter() {
    var pageResponse = PageResponse.of(List.of(kgUnit), 0, 20, 1);
    var pageRequest = new PageRequestDto(0, 20, "name", "asc");
    var criteria = new UnitCriteria(pageRequest, null, "WEIGHT", "ACTIVE");
    when(unitRepository.findAll(any(UnitCriteria.class))).thenReturn(pageResponse);

    var result = unitService.findAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.totalElements());
    assertEquals(UnitType.WEIGHT, result.content().get(0).getType());
    verify(unitRepository).findAll(any(UnitCriteria.class));
  }

  @Test
  @DisplayName("Should update unit without changing type when type is null")
  void shouldUpdateUnitWithoutChangingTypeWhenNull() {
    when(unitRepository.findById("KG")).thenReturn(kgUnit);
    when(unitRepository.save(any(UnitDomain.class))).thenReturn(kgUnit);

    var domain =
        UnitDomain.builder()
            .name("Kilogram")
            .symbol("kg")
            .description("Updated description")
            .status("INACTIVE")
            .build();
    var result = unitService.update("KG", domain);

    assertEquals("Kilogram", result.getName());
    assertEquals("kg", result.getSymbol());
    assertEquals(UnitType.WEIGHT, result.getType());
    assertEquals("Updated description", result.getDescription());
    assertEquals("INACTIVE", result.getStatus());
    verify(unitRepository).save(unitCaptor.capture());
    var saved = unitCaptor.getValue();
    assertEquals(UnitType.WEIGHT, saved.getType());
    assertEquals("Updated description", saved.getDescription());
  }

  @Test
  @DisplayName("Should update unit type when type is provided")
  void shouldUpdateUnitTypeWhenProvided() {
    when(unitRepository.findById("KG")).thenReturn(kgUnit);
    when(unitRepository.save(any(UnitDomain.class))).thenReturn(kgUnit);

    var domain =
        UnitDomain.builder()
            .name("Kilogram")
            .symbol("kg")
            .type(UnitType.VOLUME)
            .description("Unit of mass")
            .status("ACTIVE")
            .build();
    var result = unitService.update("KG", domain);

    assertEquals(UnitType.VOLUME, result.getType());
    verify(unitRepository).save(unitCaptor.capture());
    assertEquals(UnitType.VOLUME, unitCaptor.getValue().getType());
  }
}
