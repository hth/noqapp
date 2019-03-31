package com.noqapp.service;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.types.TransactionViaEnum;
import com.noqapp.repository.PayoutManager;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseOrderManagerJDBC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-03-30 17:09
 */
@Service
public class PayoutService {

    private PayoutManager payoutManager;
    private PurchaseOrderManager purchaseOrderManager;
    private PurchaseOrderManagerJDBC purchaseOrderManagerJDBC;

    @Autowired
    public PayoutService(
        PayoutManager payoutManager,
        PurchaseOrderManager purchaseOrderManager,
        PurchaseOrderManagerJDBC purchaseOrderManagerJDBC
    ) {
        this.payoutManager = payoutManager;
        this.purchaseOrderManager = purchaseOrderManager;
        this.purchaseOrderManagerJDBC = purchaseOrderManagerJDBC;
    }

    public List<PurchaseOrderEntity> currentTransactions(String bizNameId) {
        return purchaseOrderManager.findByBizNameId(bizNameId);
    }

    public List<PurchaseOrderEntity> computeEarning(String bizNameId, TransactionViaEnum transactionVia, int durationInDays) {
        return purchaseOrderManagerJDBC.computeEarning(bizNameId, transactionVia, durationInDays);
    }
}
