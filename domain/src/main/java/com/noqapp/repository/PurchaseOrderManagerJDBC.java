package com.noqapp.repository;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.SentimentTypeEnum;
import com.noqapp.domain.types.TransactionViaEnum;

import java.util.Date;
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
    boolean reviewService(String codeQR, int token, String qid, int ratingCount, String review, SentimentTypeEnum sentimentType);

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

    /** Computes historical earning. */
    List<PurchaseOrderEntity> computeEarning(String bizNameId, TransactionViaEnum transactionVia, int durationInDays);

    List<PurchaseOrderEntity> findAllOrderByCodeQR(String codeQR, int durationInDays);

    PurchaseOrderEntity findByTransactionIdAndBizStore(String transactionId, String bizStoreId);

    @Mobile
    Date clientVisitedStoreAndServicedDate(String codeQR, String qid);

    boolean hasClientVisitedThisStoreAndServiced(String codeQR, String qid);

    List<PurchaseOrderEntity> findPurchaseMadeUsingCoupon(String bizNameId);

    /**
     * Get all transaction for the day.
     *
     * @param bizNameId
     * @param from      From is the start day set to the UTC time of the store start day
     * @param until     Until is the end day set to the UTC time of the store end day
     * @return
     */
    List<PurchaseOrderEntity> findTransactionBetweenDays(String bizNameId, String from, String until);

    List<PurchaseOrderEntity> findByQidAndBizNameId(String qid, String bizNameId);

    List<PurchaseOrderEntity> findAllOrdersWhereAddressExists();

    boolean updateAddressToUserAddressId(String id, String userAddressId);
}
