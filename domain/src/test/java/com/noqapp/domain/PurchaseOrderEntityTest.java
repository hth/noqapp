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
        PurchaseOrderEntity purchaseOrderEntity = new PurchaseOrderEntity().setOrderPrice("11000").setTax("0").setGrandTotal("11000");
        assertEquals("110.00", purchaseOrderEntity.orderPriceForTransaction());
    }

    @Test
    void orderPriceWithTaxForTransaction() {
        PurchaseOrderEntity purchaseOrderEntity = new PurchaseOrderEntity().setOrderPrice("11000").setTax("50").setGrandTotal("11050");
        assertEquals("110.50", purchaseOrderEntity.orderPriceForTransaction());
    }
}
