package com.example.goodsprice.common.constant;

public final class ErrorMessageConstants {

  private ErrorMessageConstants() {}

  public static final String PRICE_NOT_FOUND_MSG = "Price not found with id: %s";
  public static final String PRODUCT_NOT_FOUND_ID_MSG = "Product not found with id: %s";
  public static final String PRODUCT_NOT_FOUND_NAME_MSG = "Product not found with name: %s";
  public static final String RECEIPT_NOT_FOUND_MSG = "Receipt not found with id: %s";
  public static final String DUPLICATE_RECEIPT_MSG = "Receipt already exists with hash: %s";
  public static final String LLM_NOT_AVAILABLE_MSG = "LLM provider is not available: %s";
  public static final String ITEMS_NOT_EMPTY_MSG = "Receipt items must not be empty";
  public static final String STORE_NOT_FOUND_MSG = "Store not found with id: %s";
  public static final String STORE_DUPLICATE_MSG =
      "Store already exists with name: %s and location: %s";
  public static final String CATEGORY_NOT_FOUND_MSG = "Category not found with id: %s";
  public static final String UNIT_NOT_FOUND_MSG = "Unit not found with id: %s";
  public static final String FEEDBACK_QUESTION_NOT_FOUND_MSG =
      "Feedback question not found with id: %s";
  public static final String ALERT_NOT_FOUND_MSG = "Alert subscription not found with id: %s";
}
