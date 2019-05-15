package com.noqapp.repository;

import com.noqapp.domain.PurchaseOrderProductEntity;

import java.util.Date;
import java.util.List;

/**
 * hitender
 * 3/29/18 2:41 PM
 */
public interface PurchaseOrderProductManager extends RepositoryManager<PurchaseOrderProductEntity> {

    PurchaseOrderProductEntity findOne(String id);

    List<PurchaseOrderProductEntity> getAllByPurchaseOrderId(String purchaseOrderId);

    List<PurchaseOrderProductEntity> getAllByPurchaseOrderIdWhenPriceZero(String purchaseOrderId);

    long deleteByCodeQR(String codeQR, Date until);

    void changePatient(String purchaseOrderId, String queueUserId);

    void removePurchaseOrderProduct(String purchaseOrderId);
}
