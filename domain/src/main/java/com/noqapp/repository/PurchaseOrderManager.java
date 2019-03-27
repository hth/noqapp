package com.noqapp.repository;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.PaymentModeEnum;
import com.noqapp.domain.types.PaymentStatusEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.TokenServiceEnum;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * hitender
 * 3/29/18 2:31 PM
 */
public interface PurchaseOrderManager extends RepositoryManager<PurchaseOrderEntity> {
    PurchaseOrderEntity findById(String id);

    PurchaseOrderEntity findBy(Set<String> qidSet, String codeQR, int tokenNumber);

    List<PurchaseOrderEntity> findAllOpenOrder(String qid);

    /** Find all clients serviced to send messages. */
    List<PurchaseOrderEntity> findAllClientOrderDelivered(int numberOfAttemptsToSendFCM);

    /** Orders that have been delivered. */
    List<PurchaseOrderEntity> findAllDeliveredHistoricalOrder(String qid);

    /** Orders that have been delivered or cancelled. */
    List<PurchaseOrderEntity> findAllPastDeliveredOrCancelledOrders(String qid);

    List<PurchaseOrderEntity> findAllOpenOrderByCodeQR(String codeQR);

    List<PurchaseOrderEntity> findAllOrderByCodeQR(String codeQR);
    List<PurchaseOrderEntity> findAllOrderByCodeQRUntil(String codeQR, Date until);

    PurchaseOrderEntity findOne(String codeQR, int tokenNumber);

    long countAllPlacedOrder(String codeQR);

    PurchaseOrderEntity getNext(String codeQR, String goTo, String sid);

    PurchaseOrderEntity getThisAsNext(String codeQR, String goTo, String sid, int tokenNumber);

    @Mobile
    PurchaseOrderEntity updateAndGetNextInQueue(
        String codeQR,
        int tokenNumber,
        PurchaseOrderStateEnum purchaseOrderState,
        String goTo,
        String sid,
        TokenServiceEnum tokenService);

    @Mobile
    boolean updateServedInQueue(
        String codeQR,
        String goTo,
        int tokenNumber,
        PurchaseOrderStateEnum purchaseOrderState,
        String sid,
        TokenServiceEnum tokenService);

    void increaseAttemptToSendNotificationCount(String id);

    long deleteByCodeQR(String codeQR);

    /** As cancellation is handled in transaction. */
    @Deprecated
    PurchaseOrderEntity cancelOrderByClient(String qid, String transactionId);
    PurchaseOrderEntity cancelOrderByClientWhenNotPaid(String qid, String transactionId);
    boolean isOrderCancelled(String qid, String transactionId);

    /** As cancellation is handled in transaction. */
    @Deprecated
    PurchaseOrderEntity cancelOrderByMerchant(String codeQR, String transactionId);
    boolean isOrderCancelled(String codeQR, int tokenNumber);

    @Mobile
    boolean reviewService(String codeQR, int token, String did, String qid, int ratingCount, String review);

    @Mobile
    List<PurchaseOrderEntity> findReviews(String codeQR);

    /** Used Internally to modify purchase order. */
    PurchaseOrderEntity findByTransactionId(String transactionId);
    PurchaseOrderEntity findByTransactionIdAndBizStore(String transactionId, String bizStoreId);

    PurchaseOrderEntity updateOnPaymentGatewayNotification(
        String transactionId,
        String transactionMessage,
        String transactionReferenceId,
        PaymentStatusEnum paymentStatus,
        PurchaseOrderStateEnum purchaseOrderState,
        PaymentModeEnum paymentMode
    );

    /** When Client says will pay on delivery. */
    PurchaseOrderEntity updateOnCashPayment(
        String transactionId,
        String transactionMessage,
        PaymentStatusEnum paymentStatus,
        PurchaseOrderStateEnum purchaseOrderState,
        PaymentModeEnum paymentMode
    );

    PurchaseOrderEntity changePatient(String transactionId, UserProfileEntity userProfile);

    PurchaseOrderEntity updateWithPartialCounterPayment(String partialPayment, String transactionId, String bizStoreId, String transactionMessage, PaymentModeEnum paymentMode);
    PurchaseOrderEntity updateWithCounterPayment(String transactionId, String bizStoreId, String transactionMessage, PaymentModeEnum paymentMode);

    void updatePurchaseOrderWithToken(int token, Date expectedServiceBegin, String transactionId);
    void removePurchaseOrderForService(String transactionId);
}
