package com.example.goodsprice.product.application.exception;

import static com.example.goodsprice.common.constant.ErrorMessageConstants.PRODUCT_NOT_FOUND_ID_MSG;
import static com.example.goodsprice.common.constant.ErrorMessageConstants.PRODUCT_NOT_FOUND_NAME_MSG;

public class ProductNotFoundException extends RuntimeException {

  public ProductNotFoundException(Long id) {
    super(PRODUCT_NOT_FOUND_ID_MSG.formatted(id));
  }

  public ProductNotFoundException(String name) {
    super(PRODUCT_NOT_FOUND_NAME_MSG.formatted(name));
  }
}
