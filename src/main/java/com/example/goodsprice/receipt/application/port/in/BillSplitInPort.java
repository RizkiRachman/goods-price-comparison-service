package com.example.goodsprice.receipt.application.port.in;

import com.example.goodsprice.receipt.application.domain.model.BillSplitRequestDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitResponseDomain;
import java.util.UUID;

public interface BillSplitInPort {

  BillSplitResponseDomain splitBill(UUID receiptId, BillSplitRequestDomain request);
}
