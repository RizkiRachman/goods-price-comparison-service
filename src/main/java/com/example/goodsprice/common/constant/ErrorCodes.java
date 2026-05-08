package com.example.goodsprice.common.constant;

public final class ErrorCodes {

  private ErrorCodes() {}

  public static final String PRICE_NOT_FOUND = "PRICE_NOT_FOUND";
  public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
  public static final String RECEIPT_NOT_FOUND = "RECEIPT_NOT_FOUND";
  public static final String DUPLICATE_RECEIPT = "DUPLICATE_RECEIPT";
  public static final String LLM_PROVIDER_UNAVAILABLE = "LLM_PROVIDER_UNAVAILABLE";
  public static final String RECEIPT_PROCESSING_FAILED = "RECEIPT_PROCESSING_FAILED";
  public static final String RATE_LIMIT_EXCEEDED = "RATE_LIMIT_EXCEEDED";
  public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
  public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
}
