package com.example.goodsprice.common.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ControllerResponse {

  private ControllerResponse() {
    // Utility class - prevent instantiation
  }

  public static <T> ResponseEntity<T> created(T body) {
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }

  public static <T> ResponseEntity<T> ok(T body) {
    return ResponseEntity.ok(body);
  }

  public static ResponseEntity<Void> noContent() {
    return ResponseEntity.noContent().build();
  }
}
