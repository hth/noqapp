package com.noqapp.service.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.noqapp.domain.types.QueueStatusEnum;

import org.junit.jupiter.api.Test;

/**
 * hitender
 * 6/3/20 11:42 PM
 */
class ServiceUtilsTest {

    @Test
    void calculateEstimatedWaitTime() {
        String estimatedWaitTime = ServiceUtils.calculateEstimatedWaitTime(
            3_00_000, //5 minutes
            5,
            QueueStatusEnum.N,
            1300,
            "Asia/Calcutta"
        );
        assertEquals("Approx 25 minutes", estimatedWaitTime);
    }
}
