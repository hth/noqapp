package com.noqapp.common.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.time.LocalTime;
import java.util.Locale;

/**
 * User: hitender
 * Date: 6/13/17 6:16 PM
 */
public class DateFormatter {
    private static final DateTimeFormatter inputFormatter = DateTimeFormat.forPattern("HHmm");
    private static final DateTimeFormatter outputFormatter = DateTimeFormat.forPattern("hh:mm a");

    private static String convertMilitaryTo12HourFormat(String rawTimestamp) {
        DateTime dateTime = inputFormatter.parseDateTime(rawTimestamp);
        return outputFormatter.print(dateTime.getMillis());
    }

    public static String convertMilitaryTo12HourFormat(int rawTimestamp) {
        return convertMilitaryTo12HourFormat(String.format(Locale.US, "%04d", rawTimestamp));
    }

    public static int getTimeIn24HourFormat(LocalTime localTime) {
        return Integer.parseInt(
            String.format(Locale.US, "%02d", localTime.getHour())
                + String.format(Locale.US, "%02d", localTime.getMinute()));
    }

    public static LocalTime getLocalTime(int hourAndMinute) {
        return LocalTime.parse(String.format(Locale.US, "%04d", hourAndMinute), java.time.format.DateTimeFormatter.ofPattern("HHmm"));
    }

    public static LocalTime addHours(LocalTime localTime, int addHour) {
        return localTime.plusHours(addHour);
    }
}
