package com.example.goodsprice.common.web;

import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.constant.HttpHeaderConstants;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.config.ratelimit.RateLimitExceededException;
import com.example.goodsprice.receipt.application.exception.DuplicateReceiptException;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RateLimitExceededException.class)
  public ResponseEntity<Map<String, Object>> handleRateLimitExceeded(RateLimitExceededException e) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaderConstants.X_RATE_LIMIT_LIMIT, String.valueOf(e.getLimit()));
    headers.add(HttpHeaderConstants.X_RATE_LIMIT_REMAINING, "0");
    headers.add(HttpHeaderConstants.RETRY_AFTER, String.valueOf(e.getRetryAfterSeconds()));
    Map<String, Object> body =
        Map.of("error", ErrorCodes.RATE_LIMIT_EXCEEDED, "message", e.getMessage());
    return new ResponseEntity<>(body, headers, HttpStatus.TOO_MANY_REQUESTS);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException e) {
    return buildResponse(HttpStatus.NOT_FOUND, e.getErrorCode(), e.getMessage());
  }

  @ExceptionHandler(DuplicateReceiptException.class)
  public ResponseEntity<Map<String, Object>> handleDuplicateReceipt(DuplicateReceiptException e) {
    return buildResponse(HttpStatus.CONFLICT, ErrorCodes.DUPLICATE_RECEIPT, e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
    return buildResponse(HttpStatus.BAD_REQUEST, ErrorCodes.VALIDATION_ERROR, e.getMessage());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> handleConstraintViolation(
      ConstraintViolationException e) {
    var messages =
        e.getConstraintViolations().stream()
            .map(v -> "%s %s".formatted(v.getPropertyPath(), v.getMessage()))
            .collect(Collectors.joining(", "));
    return buildResponse(HttpStatus.BAD_REQUEST, ErrorCodes.VALIDATION_ERROR, messages);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Map<String, Object>> handleTypeMismatch(
      MethodArgumentTypeMismatchException e) {
    var message = "Invalid value '%s' for parameter '%s'".formatted(e.getValue(), e.getName());
    return buildResponse(HttpStatus.BAD_REQUEST, ErrorCodes.VALIDATION_ERROR, message);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneral(Exception e) {
    log.error("Unhandled exception", e);
    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        ErrorCodes.INTERNAL_ERROR,
        "An unexpected error occurred");
  }

  private ResponseEntity<Map<String, Object>> buildResponse(
      HttpStatus status, String code, String message) {
    Map<String, Object> body = Map.of("error", code, "message", message);
    return new ResponseEntity<>(body, status);
  }
}
