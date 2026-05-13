package com.example.goodsprice.store.application.exception;

import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.constant.ErrorMessageConstants;
import com.example.goodsprice.common.exception.NotFoundException;

public class StoreNotFoundException extends NotFoundException {

  private static final long serialVersionUID = 1L;

  public StoreNotFoundException(Long id) {
    super(ErrorCodes.STORE_NOT_FOUND, ErrorMessageConstants.STORE_NOT_FOUND_MSG.formatted(id));
  }
}
