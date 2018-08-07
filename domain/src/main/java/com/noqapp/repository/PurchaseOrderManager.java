package com.noqapp.repository;

import com.noqapp.domain.PurchaseOrderEntity;

import java.util.List;

/**
 * hitender
 * 3/29/18 2:31 PM
 */
public interface PurchaseOrderManager extends RepositoryManager<PurchaseOrderEntity> {

    List<PurchaseOrderEntity> findAllOpenOrder(String qid);

    List<PurchaseOrderEntity> findAllOpenOrderByCodeQR(String codeQR);

    PurchaseOrderEntity findOne(String codeQR, int tokenNumber);

    long countAllPlacedOrder(String codeQR);

    PurchaseOrderEntity getNext(String codeQR, String goTo, String sid);

    PurchaseOrderEntity getThisAsNext(String codeQR, String goTo, String sid, int tokenNumber);
}
