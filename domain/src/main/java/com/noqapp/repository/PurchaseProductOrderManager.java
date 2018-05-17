package com.noqapp.repository;

import com.noqapp.domain.PurchaseOrderProductEntity;

import java.util.List;

/**
 * hitender
 * 3/29/18 2:41 PM
 */
public interface PurchaseProductOrderManager extends RepositoryManager<PurchaseOrderProductEntity> {

    List<PurchaseOrderProductEntity> getAllByPurchaseOrderId(String purchaseOrderId);
}
