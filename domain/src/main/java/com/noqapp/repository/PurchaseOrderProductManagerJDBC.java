package com.noqapp.repository;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;

import java.util.List;

/**
 * hitender
 * 9/30/18 6:02 PM
 */
public interface PurchaseOrderProductManagerJDBC {

    void batchPurchaseOrderProducts(List<PurchaseOrderProductEntity> purchaseOrderProducts);

    void rollbackPurchaseOrderProducts(List<PurchaseOrderProductEntity> purchaseOrderProducts);

    void rollbackPurchaseOrders(List<PurchaseOrderEntity> purchaseOrders);

    List<PurchaseOrderProductEntity> getByPurchaseOrderId(String purchaseOrderId);
}
