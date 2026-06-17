package com.example.goodsprice.common.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@DisplayName("AbstractCrudController should")
class AbstractCrudControllerTest {

  private static class TestController extends AbstractCrudController {
    public <T> ResponseEntity<T> doCreated(T body) {
      return created(body);
    }

    public <T> ResponseEntity<T> doOk(T body) {
      return ok(body);
    }

    public ResponseEntity<Void> doNoContent() {
      return noContent();
    }
  }

  private final TestController controller = new TestController();

  @Test
  @DisplayName("return 201 Created")
  void shouldReturnCreated() {
    var response = controller.doCreated("test");
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("test", response.getBody());
  }

  @Test
  @DisplayName("return 200 OK")
  void shouldReturnOk() {
    var response = controller.doOk("test");
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("test", response.getBody());
  }

  @Test
  @DisplayName("return 204 No Content")
  void shouldReturnNoContent() {
    var response = controller.doNoContent();
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }
}
