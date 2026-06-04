package com.example.goodsprice.common.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.constant.HttpHeaderConstants;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.config.ratelimit.RateLimitExceededException;
import com.example.goodsprice.receipt.application.exception.DuplicateReceiptException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  @Test
  void shouldHandleNotFoundException() {
    var ex = NotFoundException.product(999L);

    var response = handler.handleNotFound(ex);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assert response.getBody() != null;
    assertEquals(ErrorCodes.PRODUCT_NOT_FOUND, response.getBody().get("error"));
  }

  @Test
  void shouldHandleIllegalArgumentException() {
    var ex = new IllegalArgumentException("Invalid input");

    var response = handler.handleIllegalArgument(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assert response.getBody() != null;
    assertEquals("Invalid input", response.getBody().get("message"));
  }

  @Test
  void shouldHandleDuplicateReceiptException() {
    var ex = new DuplicateReceiptException("Receipt already exists");

    var response = handler.handleDuplicateReceipt(ex);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assert response.getBody() != null;
    assertEquals(ErrorCodes.DUPLICATE_RECEIPT, response.getBody().get("error"));
  }

  @Test
  void shouldHandleGeneralException() {
    var ex = new RuntimeException("Unexpected error");

    var response = handler.handleGeneral(ex);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assert response.getBody() != null;
    assertEquals(ErrorCodes.INTERNAL_ERROR, response.getBody().get("error"));
  }

  @Test
  void shouldHandleRateLimitExceeded() {
    var ex = new RateLimitExceededException(10, 30);

    var response = handler.handleRateLimitExceeded(ex);

    assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
    assert response.getBody() != null;
    assertEquals(ErrorCodes.RATE_LIMIT_EXCEEDED, response.getBody().get("error"));
    assertEquals("10", response.getHeaders().getFirst(HttpHeaderConstants.X_RATE_LIMIT_LIMIT));
    assertEquals("0", response.getHeaders().getFirst(HttpHeaderConstants.X_RATE_LIMIT_REMAINING));
    assertEquals("30", response.getHeaders().getFirst(HttpHeaderConstants.RETRY_AFTER));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Test
  void shouldHandleConstraintViolation() {
    var propertyPath = mock(Path.class);
    when(propertyPath.toString()).thenReturn("name");

    var violation = mock(ConstraintViolation.class);
    when(violation.getMessage()).thenReturn("must not be null");
    when(violation.getPropertyPath()).thenReturn(propertyPath);

    Set<ConstraintViolation<?>> violations = Set.of(violation);
    var ex = new ConstraintViolationException(violations);

    var response = handler.handleConstraintViolation(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assert response.getBody() != null;
    assertEquals(ErrorCodes.VALIDATION_ERROR, response.getBody().get("error"));
    assertEquals("name must not be null", response.getBody().get("message"));
  }

  @Test
  void shouldHandleTypeMismatch() {
    var ex = new MethodArgumentTypeMismatchException("abc", String.class, "age", null, null);

    var response = handler.handleTypeMismatch(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assert response.getBody() != null;
    assertEquals(ErrorCodes.VALIDATION_ERROR, response.getBody().get("error"));
    assertEquals("Invalid value 'abc' for parameter 'age'", response.getBody().get("message"));
  }

  @Test
  void shouldHandleHttpMessageNotReadable() {
    var inputMessage = mock(HttpInputMessage.class);
    var ex = new HttpMessageNotReadableException("Malformed JSON", inputMessage);

    var response = handler.handleHttpMessageNotReadable(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assert response.getBody() != null;
    assertEquals(ErrorCodes.VALIDATION_ERROR, response.getBody().get("error"));
    assertEquals("Malformed JSON or invalid request body", response.getBody().get("message"));
  }

  @Test
  void shouldHandleMethodArgumentNotValid() {
    var bindingResult = new BeanPropertyBindingResult(new Object(), "object");
    bindingResult.addError(new FieldError("object", "name", "must not be blank"));
    bindingResult.addError(new FieldError("object", "price", "must be positive"));
    var ex = new MethodArgumentNotValidException(null, bindingResult);

    var response = handler.handleMethodArgumentNotValid(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assert response.getBody() != null;
    assertEquals(ErrorCodes.VALIDATION_ERROR, response.getBody().get("error"));
    assertEquals(
        "name must not be blank, price must be positive", response.getBody().get("message"));
  }
}
