package com.noqapp.common.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

/**
 * User: hitender
 * Date: 6/13/17 6:16 PM
 */
public class DateFormatter {
    private static DateTimeFormatter inputFormatter = DateTimeFormat.forPattern("HHmm");
    private static DateTimeFormatter outputFormatter = DateTimeFormat.forPattern("hh:mma");

    private static String convertMilitaryTo12HourFormat(String rawTimestamp) {
        DateTime dateTime = inputFormatter.parseDateTime(rawTimestamp);
        return outputFormatter.print(dateTime.getMillis());
    }

    public static String convertMilitaryTo12HourFormat(int rawTimestamp) {
        return convertMilitaryTo12HourFormat(String.format(Locale.US, "%04d", rawTimestamp));
    }
}
