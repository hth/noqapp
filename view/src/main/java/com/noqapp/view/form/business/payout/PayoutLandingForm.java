package com.noqapp.view.form.business.payout;

import com.noqapp.domain.PurchaseOrderEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-03-30 17:32
 */
public class PayoutLandingForm {

    private String timeZone;
    private List<PurchaseOrderEntity> purchaseOrders;

    public String getTimeZone() {
        return timeZone;
    }

    public PayoutLandingForm setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public List<PurchaseOrderEntity> getPurchaseOrders() {
        return purchaseOrders;
    }

    public PayoutLandingForm setPurchaseOrders(List<PurchaseOrderEntity> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
        return this;
    }
}
