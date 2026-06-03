package com.example.goodsprice.common.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.config.ratelimit.RateLimitExceededException;
import com.example.goodsprice.receipt.application.exception.DuplicateReceiptException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
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
    assertEquals(ErrorCodes.PRODUCT_NOT_FOUND, response.getBody().get("error"));
  }

  @Test
  void shouldHandleIllegalArgumentException() {
    var ex = new IllegalArgumentException("Invalid input");

    var response = handler.handleIllegalArgument(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid input", response.getBody().get("message"));
  }

  @Test
  void shouldHandleDuplicateReceiptException() {
    var ex = new DuplicateReceiptException("Receipt already exists");

    var response = handler.handleDuplicateReceipt(ex);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals(ErrorCodes.DUPLICATE_RECEIPT, response.getBody().get("error"));
  }

  @Test
  void shouldHandleGeneralException() {
    var ex = new RuntimeException("Unexpected error");

    var response = handler.handleGeneral(ex);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(ErrorCodes.INTERNAL_ERROR, response.getBody().get("error"));
  }
}
