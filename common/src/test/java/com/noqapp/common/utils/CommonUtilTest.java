package com.noqapp.common.utils;

import static org.junit.jupiter.api.Assertions.*;

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
    void calculateAge() {
        assertEquals("33+ years", CommonUtil.calculateAge("1986-03-07"));
        assertEquals("13+ months", CommonUtil.calculateAge("2018-03-07"));
        assertEquals("49+ days", CommonUtil.calculateAge("2019-03-07"));
    }
}