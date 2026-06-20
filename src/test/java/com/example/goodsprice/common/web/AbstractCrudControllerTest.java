package com.example.goodsprice.common.web;

import static com.example.goodsprice.common.web.ControllerResponse.created;
import static com.example.goodsprice.common.web.ControllerResponse.noContent;
import static com.example.goodsprice.common.web.ControllerResponse.ok;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@DisplayName("ControllerResponse should")
class AbstractCrudControllerTest {

  @Test
  @DisplayName("return 201 Created")
  void shouldReturnCreated() {
    ResponseEntity<String> response = created("test");
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("test", response.getBody());
  }

  @Test
  @DisplayName("return 200 OK")
  void shouldReturnOk() {
    ResponseEntity<String> response = ok("test");
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("test", response.getBody());
  }

  @Test
  @DisplayName("return 204 No Content")
  void shouldReturnNoContent() {
    ResponseEntity<Void> response = noContent();
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }
}
