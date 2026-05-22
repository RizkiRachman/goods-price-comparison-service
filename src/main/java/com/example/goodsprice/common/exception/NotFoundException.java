package com.example.goodsprice.common.exception;

import static com.example.goodsprice.common.constant.ErrorCodes.PRICE_NOT_FOUND;
import static com.example.goodsprice.common.constant.ErrorCodes.PRODUCT_NOT_FOUND;
import static com.example.goodsprice.common.constant.ErrorCodes.RECEIPT_NOT_FOUND;
import static com.example.goodsprice.common.constant.ErrorCodes.STORE_NOT_FOUND;
import static com.example.goodsprice.common.constant.ErrorMessageConstants.PRICE_NOT_FOUND_MSG;
import static com.example.goodsprice.common.constant.ErrorMessageConstants.PRODUCT_NOT_FOUND_ID_MSG;
import static com.example.goodsprice.common.constant.ErrorMessageConstants.PRODUCT_NOT_FOUND_NAME_MSG;
import static com.example.goodsprice.common.constant.ErrorMessageConstants.RECEIPT_NOT_FOUND_MSG;
import static com.example.goodsprice.common.constant.ErrorMessageConstants.STORE_NOT_FOUND_MSG;

import java.util.UUID;

public class NotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final String errorCode;

  public NotFoundException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public static NotFoundException price(Long id) {
    return new NotFoundException(PRICE_NOT_FOUND, PRICE_NOT_FOUND_MSG.formatted(id));
  }

  public static NotFoundException product(Long id) {
    return new NotFoundException(PRODUCT_NOT_FOUND, PRODUCT_NOT_FOUND_ID_MSG.formatted(id));
  }

  public static NotFoundException product(String name) {
    return new NotFoundException(PRODUCT_NOT_FOUND, PRODUCT_NOT_FOUND_NAME_MSG.formatted(name));
  }

  public static NotFoundException receipt(UUID id) {
    return new NotFoundException(RECEIPT_NOT_FOUND, RECEIPT_NOT_FOUND_MSG.formatted(id));
  }

  public static NotFoundException store(Long id) {
    return new NotFoundException(STORE_NOT_FOUND, STORE_NOT_FOUND_MSG.formatted(id));
  }

  public String getErrorCode() {
    return errorCode;
  }
}
