package com.example.goodsprice.common.web;

import org.springframework.http.ResponseEntity;

/**
 * Abstract base controller providing standard CRUD response wrappers.
 *
 * <p>Subclasses define their own public API methods to satisfy the generated OpenAPI interface, but
 * delegate to these helpers for response wrapping.
 */
public class AbstractCrudController {

  protected static <T> ResponseEntity<T> created(T body) {
    return ControllerResponse.created(body);
  }

  protected static <T> ResponseEntity<T> ok(T body) {
    return ControllerResponse.ok(body);
  }

  protected static ResponseEntity<Void> noContent() {
    return ControllerResponse.noContent();
  }
}
