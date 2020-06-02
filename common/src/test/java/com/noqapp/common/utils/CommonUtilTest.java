package com.noqapp.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * hitender
 * 2019-01-09 14:32
 */
class CommonUtilTest {

    @Test
    void generateTransactionId() {
        String StoreId = CommonUtil.generateHexFromObjectId();
        String transactionId_1 = CommonUtil.generateTransactionId(StoreId, 5);
        String transactionId_2 = CommonUtil.generateTransactionId(StoreId, 5);

        assertNotEquals(transactionId_1, transactionId_2);
    }

    @Test
    void abbreviateName() {
        assertEquals("James B" , CommonUtil.abbreviateName("1", "James Bond"));
        assertEquals("James B" , CommonUtil.abbreviateName("1", "James Bond William"));
//        assertEquals("Pankaj  Kumar Singh" , CommonUtil.abbreviateName("1", "Pankaj  Kumar Singh"));
    }
}
