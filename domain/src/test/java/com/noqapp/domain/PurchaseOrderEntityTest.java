package com.noqapp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * hitender
 * 2019-03-01 23:33
 */
class PurchaseOrderEntityTest {

    @Test
    void orderPriceForTransaction() {
        PurchaseOrderEntity purchaseOrderEntity = new PurchaseOrderEntity().setOrderPrice("11000");
        assertEquals("110.00", purchaseOrderEntity.orderPriceForTransaction());
    }
}