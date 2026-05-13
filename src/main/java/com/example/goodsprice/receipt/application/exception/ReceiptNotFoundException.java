package com.example.goodsprice.receipt.application.exception;

import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.constant.ErrorMessageConstants;
import com.example.goodsprice.common.exception.NotFoundException;
import java.util.UUID;

public class ReceiptNotFoundException extends NotFoundException {

  private static final long serialVersionUID = 1L;

  public ReceiptNotFoundException(UUID id) {
    super(ErrorCodes.RECEIPT_NOT_FOUND, ErrorMessageConstants.RECEIPT_NOT_FOUND_MSG.formatted(id));
  }

  public ReceiptNotFoundException(String errorCode, String message) {
    super(errorCode, message);
  }
}
