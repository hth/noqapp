package com.noqapp.view.form.business.payout;

import com.noqapp.domain.PurchaseOrderEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-03-30 17:32
 */
public class PayoutLandingForm {

    private List<PurchaseOrderEntity> purchaseOrders;

    public List<PurchaseOrderEntity> getPurchaseOrders() {
        return purchaseOrders;
    }

    public PayoutLandingForm setPurchaseOrders(List<PurchaseOrderEntity> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
        return this;
    }
}
