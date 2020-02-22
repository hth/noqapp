package com.noqapp.common.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

/**
 * hitender
 * 2/21/20 9:05 PM
 */
class DateFormatterTest {

    @Test
    void addHours() {
        int startHour = 830;
        LocalTime localTime = DateFormatter.addHours(DateFormatter.getLocalTime(730), 2);
        assertTrue(DateFormatter.getTimeIn24HourFormat(localTime) > startHour);

        startHour = 830;
        localTime = DateFormatter.addHours(DateFormatter.getLocalTime(630), 2);
        assertFalse(DateFormatter.getTimeIn24HourFormat(localTime) > startHour);

        startHour = 1830;
        localTime = DateFormatter.addHours(DateFormatter.getLocalTime(1730), 2);
        assertTrue(DateFormatter.getTimeIn24HourFormat(localTime) > startHour);

        startHour = 1830;
        localTime = DateFormatter.addHours(DateFormatter.getLocalTime(1630), 2);
        assertFalse(DateFormatter.getTimeIn24HourFormat(localTime) > startHour);
    }
}