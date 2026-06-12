package com.example.goodsprice.unit.infrastructure.adapter.web;

import com.example.goodsprice.api.controller.UnitsApi;
import com.example.goodsprice.api.model.CreateUnitRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Unit;
import com.example.goodsprice.api.model.UnitListResponse;
import com.example.goodsprice.api.model.UpdateUnitRequest;
import com.example.goodsprice.common.web.ControllerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UnitController implements UnitsApi {

  private final UnitWebAdapter adapter;

  @Override
  public ResponseEntity<Unit> createUnit(@Valid CreateUnitRequest request) {
    var unit = adapter.create(request);
    return ControllerResponse.created(unit);
  }

  @Override
  public ResponseEntity<Unit> getUnit(String unitId) {
    return ControllerResponse.ok(adapter.findById(unitId));
  }

  @Override
  public ResponseEntity<UnitListResponse> listUnits(
      Integer page,
      Integer pageSize,
      String search,
      String type,
      EntityStatus status,
      String sortBy,
      String sortOrder) {
    return ControllerResponse.ok(
        adapter.list(page, pageSize, search, type, status, sortBy, sortOrder));
  }

  @Override
  public ResponseEntity<Unit> updateUnit(String unitId, @Valid UpdateUnitRequest request) {
    return ControllerResponse.ok(adapter.update(unitId, request));
  }
}
