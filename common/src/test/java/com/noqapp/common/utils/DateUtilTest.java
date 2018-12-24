package com.noqapp.common.utils;

import static com.noqapp.common.utils.DateUtil.DAY.TODAY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.joda.time.DateTime;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.TimeZone;

/**
 * hitender
 * 9/13/18 6:56 PM
 */
class DateUtilTest {

    @Test
    void isThisDayBetween_Inclusive() {
        DateTime midnight = DateUtil.midnight(DateTime.now());
        assertEquals(true, DateUtil.isThisDayBetween(midnight.toDate(), midnight.minusDays(1).toDate(), midnight.plusDays(1).toDate()));
        assertEquals(true, DateUtil.isThisDayBetween(midnight.plusDays(1).toDate(), midnight.minusDays(1).toDate(), midnight.plusDays(1).toDate()));
        assertEquals(true, DateUtil.isThisDayBetween(midnight.minusDays(1).toDate(), midnight.minusDays(1).toDate(), midnight.plusDays(1).toDate()));
    }

    @Test
    void isThisDayBetween_Excluding() {
        DateTime midnight = DateUtil.midnight(DateTime.now());
        assertEquals(false, DateUtil.isThisDayBetween(midnight.plusDays(2).toDate(), midnight.minusDays(1).toDate(), midnight.plusDays(1).toDate()));
        assertEquals(false, DateUtil.isThisDayBetween(midnight.minusDays(2).toDate(), midnight.minusDays(1).toDate(), midnight.plusDays(1).toDate()));
    }

    @Test
    void computeNextRunTimeAtUTC_Match_Time() {
        ZonedDateTime nyc = DateUtil.computeNextRunTimeAtUTC(TimeZone.getTimeZone("America/New_York"), 20, 0, TODAY);
        ZonedDateTime pst = DateUtil.computeNextRunTimeAtUTC(TimeZone.getTimeZone("PST"), 17, 0, TODAY);
        assertEquals(nyc, pst, "Both dates should be same");
    }
}