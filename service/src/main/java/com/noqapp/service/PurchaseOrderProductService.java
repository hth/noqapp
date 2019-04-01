package com.noqapp.service;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonPurchaseOrder;
import com.noqapp.repository.PurchaseOrderProductManager;
import com.noqapp.repository.PurchaseOrderProductManagerJDBC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-04-01 12:25
 */
@Service
public class PurchaseOrderProductService {

    private PurchaseOrderProductManager purchaseOrderProductManager;
    private PurchaseOrderProductManagerJDBC purchaseOrderProductManagerJDBC;

    @Autowired
    public PurchaseOrderProductService(
        PurchaseOrderProductManager purchaseOrderProductManager,
        PurchaseOrderProductManagerJDBC purchaseOrderProductManagerJDBC
    ) {
        this.purchaseOrderProductManager = purchaseOrderProductManager;
        this.purchaseOrderProductManagerJDBC = purchaseOrderProductManagerJDBC;
    }

    @Mobile
    public JsonPurchaseOrder populateJsonPurchaseOrder(PurchaseOrderEntity purchaseOrder) {
        List<PurchaseOrderProductEntity> products = purchaseOrderProductManager.getAllByPurchaseOrderId(purchaseOrder.getId());
        return new JsonPurchaseOrder(purchaseOrder, products);
    }

    @Mobile
    public JsonPurchaseOrder populateHistoricalJsonPurchaseOrder(PurchaseOrderEntity purchaseOrder) {
        List<PurchaseOrderProductEntity> products = purchaseOrderProductManagerJDBC.getByPurchaseOrderId(purchaseOrder.getId());
        return new JsonPurchaseOrder(purchaseOrder, products);
    }
}
