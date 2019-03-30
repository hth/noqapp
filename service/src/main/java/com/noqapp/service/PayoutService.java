package com.noqapp.service;

import com.noqapp.domain.PurchaseOrderEntity;
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

    private PurchaseOrderManager purchaseOrderManager;
    private PurchaseOrderManagerJDBC purchaseOrderManagerJDBC;

    @Autowired
    public PayoutService(PurchaseOrderManager purchaseOrderManager, PurchaseOrderManagerJDBC purchaseOrderManagerJDBC) {
        this.purchaseOrderManager = purchaseOrderManager;
        this.purchaseOrderManagerJDBC = purchaseOrderManagerJDBC;
    }

    public List<PurchaseOrderEntity> currentTransactions(String bizNameId) {
        return purchaseOrderManager.findByBizNameId(bizNameId);
    }
}
