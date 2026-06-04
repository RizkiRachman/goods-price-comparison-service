package com.example.goodsprice.unit.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.CreateUnitRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Unit;
import com.example.goodsprice.api.model.UnitListResponse;
import com.example.goodsprice.api.model.UpdateUnitRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UnitControllerTest {

  @Mock private UnitWebAdapter adapter;

  @InjectMocks private UnitController controller;

  private Unit apiUnit;

  @BeforeEach
  void setUp() {
    apiUnit = new Unit();
    apiUnit.setId("KG");
    apiUnit.setName("Kilogram");
  }

  @Test
  @DisplayName("Should create unit via controller")
  void shouldCreateUnit() {
    var request = new CreateUnitRequest();
    request.setId("KG");
    request.setName("Kilogram");

    when(adapter.create(request)).thenReturn(apiUnit);

    var response = controller.createUnit(request);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("KG", response.getBody().getId());
    verify(adapter).create(request);
  }

  @Test
  @DisplayName("Should get unit by id")
  void shouldGetUnit() {
    when(adapter.findById("KG")).thenReturn(apiUnit);

    var response = controller.getUnit("KG");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("KG", response.getBody().getId());
    verify(adapter).findById("KG");
  }

  @Test
  @DisplayName("Should list units")
  void shouldListUnits() {
    var listResponse = new UnitListResponse();

    when(adapter.list(1, 20, "search", "WEIGHT", EntityStatus.APPROVED, "name", "asc"))
        .thenReturn(listResponse);

    var response =
        controller.listUnits(1, 20, "search", "WEIGHT", EntityStatus.APPROVED, "name", "asc");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @DisplayName("Should update unit")
  void shouldUpdateUnit() {
    var request = new UpdateUnitRequest();
    request.setName("Kilogram Updated");

    when(adapter.update("KG", request)).thenReturn(apiUnit);

    var response = controller.updateUnit("KG", request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(adapter).update("KG", request);
  }
}
