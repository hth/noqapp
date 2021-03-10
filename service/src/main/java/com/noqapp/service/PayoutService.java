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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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

    public List<PurchaseOrderEntity> findTransactionOnDay(String bizNameId, String day, String timeZone) {
        Assert.isTrue(DateUtil.DOB_PATTERN.matcher(day).matches(), "Day pattern does not match");
        /* UTC day to datetime zone of the business. As transaction are based on the business time zone. */
        ZonedDateTime zonedDateTimeOfBusiness = LocalDate.parse(day).atStartOfDay().atZone(ZoneId.of(timeZone));
        List<PurchaseOrderEntity> purchaseOrders = purchaseOrderManagerJDBC.findTransactionBetweenDays(
            bizNameId,
            DateUtil.getLocalDateTimeToMySQLDate(zonedDateTimeOfBusiness.minus(1, ChronoUnit.SECONDS)),
            DateUtil.getLocalDateTimeToMySQLDate(zonedDateTimeOfBusiness.plusDays(1).minus(1, ChronoUnit.SECONDS)));

        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(purchaseOrder.getQueueUserId());
            purchaseOrder
                .setCustomerPhone(StringUtils.isNotBlank(userProfile.getGuardianPhone())
                    ? userProfile.getGuardianPhone()
                    : userProfile.getPhone())
                .setCustomerName(userProfile.getName());
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
