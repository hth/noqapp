package com.noqapp.service;

import com.noqapp.repository.BizStoreManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.TimeZone;

/**
 * User: hitender
 * Date: 6/16/17 9:36 PM
 */
class ExternalServiceTest {

    @Mock private BizStoreManager bizStoreManager;
    private ExternalService externalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        externalService = new ExternalService("", 0, bizStoreManager);
    }

    @Test
    void computeNextRunTimeAtUTC_Match_Time() {
        Date nyc = externalService.computeNextRunTimeAtUTC(TimeZone.getTimeZone("America/New_York"), 20, 0);
        Date pst = externalService.computeNextRunTimeAtUTC(TimeZone.getTimeZone("PST"), 17, 0);
        Assertions.assertEquals(nyc, pst, "Both dates should be same");
    }
}