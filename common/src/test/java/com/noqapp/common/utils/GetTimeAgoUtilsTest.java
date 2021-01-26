package com.noqapp.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * hitender
 * 10/28/20 3:33 PM
 */
class GetTimeAgoUtilsTest {

    @Test
    void getTimeAgo() {
        //1 Second
        assertEquals("Few minutes", GetTimeAgoUtils.getTimeAgo(1000));

        //10 Second
        assertEquals("Few minutes", GetTimeAgoUtils.getTimeAgo(10_000));

        //100 Second also 1.6 Minutes
        assertEquals("Few minutes", GetTimeAgoUtils.getTimeAgo(100_000));

        //100 Second also 16 Minutes
        assertEquals("Approx 16 minutes", GetTimeAgoUtils.getTimeAgo(1_000_000));

        //1 hour
        assertEquals("Approx an hour", GetTimeAgoUtils.getTimeAgo(3_000_000));

        //2.77 hours
        assertEquals("Approx 2 hours", GetTimeAgoUtils.getTimeAgo(10_000_000));

        //13 hours
        assertEquals("Approx 13 hours", GetTimeAgoUtils.getTimeAgo(50_000_000));

        //27.77 hours
        assertEquals("More than a day", GetTimeAgoUtils.getTimeAgo(100_000_000));
    }
}
