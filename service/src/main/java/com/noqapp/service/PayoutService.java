package com.noqapp.service;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.types.TransactionViaEnum;
import com.noqapp.repository.PayoutManager;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseOrderManagerJDBC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDate;
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

    public List<PurchaseOrderEntity> findTransactionOnDay(String bizNameId, String day) {
        Assert.isTrue(DateUtil.DOB_PATTERN.matcher(day).matches(), "Day pattern does not match");
        LocalDate localDate = LocalDate.parse(day);
        LocalDate until = localDate.plusDays(1);
        return purchaseOrderManagerJDBC.findTransactionBetweenDays(bizNameId, DateUtil.asDate(localDate), DateUtil.asDate(until));
    }

    public List<PurchaseOrderEntity> findPurchaseMadeUsingCoupon(String bizNameId) {
        List<PurchaseOrderEntity> purchaseOrders = purchaseOrderManager.findPurchaseMadeUsingCoupon(bizNameId);
        List<PurchaseOrderEntity> purchaseOrdersHistorical = purchaseOrderManagerJDBC.findPurchaseMadeUsingCoupon(bizNameId);
        if (null != purchaseOrdersHistorical && !purchaseOrdersHistorical.isEmpty()) {
            purchaseOrders.addAll(purchaseOrdersHistorical);
        }
        return purchaseOrdersHistorical;
    }
}
