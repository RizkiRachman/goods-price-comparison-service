package com.example.goodsprice.receipt.application.port.in;

import com.example.goodsprice.api.model.BillSplitRequest;
import com.example.goodsprice.api.model.BillSplitResponse;
import java.util.UUID;

public interface BillSplitInPort {

  BillSplitResponse splitBill(UUID receiptId, BillSplitRequest request);
}
