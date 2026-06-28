package com.example.goodsprice.unit.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.CreateUnitRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Unit;
import com.example.goodsprice.api.model.UpdateUnitRequest;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import com.example.goodsprice.unit.application.domain.model.UnitType;
import com.example.goodsprice.unit.application.port.in.UnitInPort;
import com.example.goodsprice.unit.application.port.in.dto.UnitCriteria;
import com.example.goodsprice.unit.infrastructure.adapter.web.mapper.UnitDtoMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnitWebAdapterTest {

  @Mock private UnitInPort unitInPort;
  @Mock private UnitDtoMapper mapper;

  @InjectMocks private UnitWebAdapter unitWebAdapter;

  @Captor private ArgumentCaptor<UnitCriteria> criteriaCaptor;

  private UnitDomain unitDomain;
  private Unit apiUnit;

  @BeforeEach
  void setUp() {
    unitDomain =
        UnitDomain.builder()
            .id("KG")
            .name("Kilogram")
            .symbol("kg")
            .type(UnitType.WEIGHT)
            .description("Unit of mass")
            .status("ACTIVE")
            .build();

    apiUnit = new Unit();
    apiUnit.setId("KG");
    apiUnit.setName("Kilogram");
  }

  @Test
  @DisplayName("Should create unit from request")
  void shouldCreateUnit() {
    var request = new CreateUnitRequest();
    request.setId("KG");
    request.setName("Kilogram");
    request.setSymbol("kg");
    request.setType(CreateUnitRequest.TypeEnum.WEIGHT);
    request.setDescription("Unit of mass");

    when(unitInPort.create(any(UnitDomain.class))).thenReturn(unitDomain);
    when(mapper.toApiUnit(unitDomain)).thenReturn(apiUnit);

    var result = unitWebAdapter.create(request);

    assertNotNull(result);
    assertEquals("KG", result.getId());
    verify(unitInPort).create(any(UnitDomain.class));
  }

  @Test
  @DisplayName("Should find unit by id")
  void shouldFindById() {
    when(unitInPort.findById("KG")).thenReturn(unitDomain);
    when(mapper.toApiUnit(unitDomain)).thenReturn(apiUnit);

    var result = unitWebAdapter.findById("KG");

    assertNotNull(result);
    assertEquals("KG", result.getId());
  }

  @Test
  @DisplayName("Should list units with pagination")
  void shouldListUnits() {
    var pageResponse = PageResponse.of(List.of(unitDomain), 1, 20, 1);
    when(unitInPort.findAll(any(UnitCriteria.class))).thenReturn(pageResponse);
    when(mapper.toApiUnit(unitDomain)).thenReturn(apiUnit);

    var result = unitWebAdapter.list(1, 20, "kilo", "WEIGHT", EntityStatus.APPROVED, "name", "asc");

    assertNotNull(result);
    assertEquals(1, result.getData().size());
    assertEquals(1, result.getPagination().getTotalItems());
  }

  @Test
  @DisplayName("Should list units with default pagination when null")
  void shouldListUnitsWithDefaults() {
    var pageResponse = PageResponse.of(List.of(unitDomain), 0, 20, 1);
    when(unitInPort.findAll(any(UnitCriteria.class))).thenReturn(pageResponse);
    when(mapper.toApiUnit(unitDomain)).thenReturn(apiUnit);

    var result = unitWebAdapter.list(null, null, null, null, null, null, null);

    assertNotNull(result);
    assertEquals(1, result.getData().size());
  }

  @Test
  @DisplayName("Should create unit with null type")
  void shouldCreateUnitWithNullType() {
    var request = new CreateUnitRequest();
    request.setId("PC");
    request.setName("Piece");
    request.setSymbol("pc");
    request.setType(null);

    var nullTypeDomain =
        UnitDomain.builder()
            .id("PC")
            .name("Piece")
            .symbol("pc")
            .type(null)
            .description(null)
            .build();
    when(unitInPort.create(any(UnitDomain.class))).thenReturn(nullTypeDomain);
    when(mapper.toApiUnit(nullTypeDomain))
        .thenAnswer(
            inv -> {
              var u = new Unit();
              u.setId("PC");
              u.setName("Piece");
              return u;
            });

    var result = unitWebAdapter.create(request);

    assertNotNull(result);
    assertEquals("PC", result.getId());
    verify(unitInPort).create(any(UnitDomain.class));
  }

  @Test
  @DisplayName("Should update unit")
  void shouldUpdateUnit() {
    var request = new UpdateUnitRequest();
    request.setName("Kilogram Updated");

    when(unitInPort.update(ArgumentMatchers.eq("KG"), any(UnitDomain.class)))
        .thenReturn(unitDomain);
    when(mapper.toApiUnit(unitDomain)).thenReturn(apiUnit);

    var result = unitWebAdapter.update("KG", request);

    assertNotNull(result);
    assertEquals("KG", result.getId());
    verify(unitInPort).update(ArgumentMatchers.eq("KG"), any(UnitDomain.class));
  }

  @Test
  @DisplayName("Should update unit with null fields")
  void shouldUpdateUnitWithNullFields() {
    var request = new UpdateUnitRequest();
    request.setName("Kilogram Updated");
    request.setType(null);
    request.setSymbol(null);
    request.setDescription(null);
    request.setStatus(null);

    when(unitInPort.update(ArgumentMatchers.eq("KG"), any(UnitDomain.class)))
        .thenReturn(unitDomain);
    when(mapper.toApiUnit(unitDomain)).thenReturn(apiUnit);

    var result = unitWebAdapter.update("KG", request);

    assertNotNull(result);
    assertEquals("KG", result.getId());
    verify(unitInPort).update(ArgumentMatchers.eq("KG"), any(UnitDomain.class));
  }
}
