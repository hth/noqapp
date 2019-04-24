package com.noqapp.repository;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.TransactionViaEnum;

import java.util.List;

/**
 * hitender
 * 9/30/18 6:01 PM
 */
public interface PurchaseOrderManagerJDBC {

    void batchPurchaseOrder(List<PurchaseOrderEntity> purchaseOrders);

    void rollbackPurchaseOrder(List<PurchaseOrderEntity> purchaseOrders);

    List<PurchaseOrderEntity> getByQid(String qid, BusinessTypeEnum ignoreBusinessType);

    @Mobile
    boolean reviewService(String codeQR, int token, String did, String qid, int ratingCount, String review);

    @Mobile
    List<PurchaseOrderEntity> findReviews(String codeQR, int reviewLimitedToDays);

    @Mobile
    List<PurchaseOrderEntity> findAllOrderWithState(String qid, PurchaseOrderStateEnum purchaseOrderState);

    @Mobile
    PurchaseOrderEntity findOrderByTransactionId(String qid, String transactionId);

    /** Used Internally to modify purchase order. And to lookup purchase order. */
    PurchaseOrderEntity findOrderByTransactionId(String transactionId);

    @Mobile
    void deleteById(String id);

    List<PurchaseOrderEntity> computeEarning(String bizNameId, TransactionViaEnum transactionVia, int durationInDays);

    List<PurchaseOrderEntity> findAllOrderByCodeQR(String codeQR, int durationInDays);
    PurchaseOrderEntity findByTransactionIdAndBizStore(String transactionId, String bizStoreId);
}
