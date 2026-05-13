package com.example.goodsprice.product.application.exception;

import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.constant.ErrorMessageConstants;
import com.example.goodsprice.common.exception.NotFoundException;

public class ProductNotFoundException extends NotFoundException {

  private static final long serialVersionUID = 1L;

  public ProductNotFoundException(Long id) {
    super(
        ErrorCodes.PRODUCT_NOT_FOUND, ErrorMessageConstants.PRODUCT_NOT_FOUND_ID_MSG.formatted(id));
  }

  public ProductNotFoundException(String name) {
    super(
        ErrorCodes.PRODUCT_NOT_FOUND,
        ErrorMessageConstants.PRODUCT_NOT_FOUND_NAME_MSG.formatted(name));
  }
}
