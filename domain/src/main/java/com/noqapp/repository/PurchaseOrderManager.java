package com.noqapp.repository;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.TokenServiceEnum;

import java.util.List;

/**
 * hitender
 * 3/29/18 2:31 PM
 */
public interface PurchaseOrderManager extends RepositoryManager<PurchaseOrderEntity> {
    PurchaseOrderEntity findById(String id);

    List<PurchaseOrderEntity> findAllOpenOrder(String qid);

    /** Find all clients serviced to send messages. */
    List<PurchaseOrderEntity> findAllClientOrderDelivered(int numberOfAttemptsToSendFCM);

    /** Orders that have been delivered. */
    List<PurchaseOrderEntity> findAllHistoricalOrder(String qid);

    List<PurchaseOrderEntity> findAllOpenOrderByCodeQR(String codeQR);

    List<PurchaseOrderEntity> findAllOrderByCodeQR(String codeQR);

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
}
