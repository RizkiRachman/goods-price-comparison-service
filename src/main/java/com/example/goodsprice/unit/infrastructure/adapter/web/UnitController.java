package com.example.goodsprice.unit.infrastructure.adapter.web;

import com.example.goodsprice.api.controller.UnitsApi;
import com.example.goodsprice.api.model.CreateUnitRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Unit;
import com.example.goodsprice.api.model.UnitListResponse;
import com.example.goodsprice.api.model.UpdateUnitRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UnitController implements UnitsApi {

  private final UnitWebAdapter adapter;

  @Override
  public ResponseEntity<Unit> createUnit(CreateUnitRequest request) {
    var unit = adapter.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(unit);
  }

  @Override
  public ResponseEntity<Unit> getUnit(String unitId) {
    return ResponseEntity.ok(adapter.findById(unitId));
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
    return ResponseEntity.ok(adapter.list(page, pageSize, search, type, status, sortBy, sortOrder));
  }

  @Override
  public ResponseEntity<Unit> updateUnit(String unitId, UpdateUnitRequest request) {
    return ResponseEntity.ok(adapter.update(unitId, request));
  }
}
