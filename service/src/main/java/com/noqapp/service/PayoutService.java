package com.noqapp.service;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.types.TransactionViaEnum;
import com.noqapp.repository.PayoutManager;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseOrderManagerJDBC;
import com.noqapp.repository.UserProfileManager;

import org.apache.commons.lang3.StringUtils;

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
    private UserProfileManager userProfileManager;

    @Autowired
    public PayoutService(
        PayoutManager payoutManager,
        PurchaseOrderManager purchaseOrderManager,
        PurchaseOrderManagerJDBC purchaseOrderManagerJDBC,
        UserProfileManager userProfileManager
    ) {
        this.payoutManager = payoutManager;
        this.purchaseOrderManager = purchaseOrderManager;
        this.purchaseOrderManagerJDBC = purchaseOrderManagerJDBC;
        this.userProfileManager = userProfileManager;
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
        List<PurchaseOrderEntity> purchaseOrders = purchaseOrderManagerJDBC.findTransactionBetweenDays(bizNameId, DateUtil.asDate(localDate), DateUtil.asDate(localDate.plusDays(1)));
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(purchaseOrder.getQueueUserId());
            purchaseOrder.setCustomerPhone(StringUtils.isNotBlank(userProfile.getGuardianPhone()) ? userProfile.getGuardianPhone() : userProfile.getPhone());
            purchaseOrder.setCustomerName(userProfile.getName());
        }

        return purchaseOrders;
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
