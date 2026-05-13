package com.example.goodsprice.common.exception;

public class NotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final String errorCode;

  public NotFoundException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }
}
