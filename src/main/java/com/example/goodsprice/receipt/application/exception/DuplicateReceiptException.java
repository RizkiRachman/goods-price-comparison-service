package com.example.goodsprice.receipt.application.exception;

import static com.example.goodsprice.common.constant.ErrorMessageConstants.DUPLICATE_RECEIPT_MSG;

public class DuplicateReceiptException extends RuntimeException {

  public DuplicateReceiptException(String imageHash) {
    super(DUPLICATE_RECEIPT_MSG.formatted(imageHash));
  }
}
