package com.noqapp.repository;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.annotation.Mobile;

import java.util.List;

/**
 * hitender
 * 9/30/18 6:01 PM
 */
public interface PurchaseOrderManagerJDBC {

    void batchPurchaseOrder(List<PurchaseOrderEntity> purchaseOrders);

    void rollbackPurchaseOrder(List<PurchaseOrderEntity> purchaseOrders);

    List<PurchaseOrderEntity> getByQid(String qid);

    @Mobile
    boolean reviewService(String codeQR, int token, String did, String qid, int ratingCount, String review);

    @Mobile
    List<PurchaseOrderEntity> findReviews(String codeQR, int reviewLimitedToDays);
}
