package com.noqapp.common.utils;

import static com.noqapp.common.utils.DateUtil.HOURS;
import static com.noqapp.common.utils.DateUtil.MINUTE_IN_SECONDS;

/**
 * hitender
 * 6/3/20 8:50 PM
 */
public class GetTimeAgoUtils {
    public static final int SECOND_MILLIS = 1_000;
    private static final int MINUTE_MILLIS = MINUTE_IN_SECONDS * SECOND_MILLIS;
    private static final int HOUR_MILLIS = MINUTE_IN_SECONDS * MINUTE_MILLIS;
    private static final int DAY_MILLIS = HOURS * HOUR_MILLIS;

    public static String getTimeAgo(long time) {

        if (time == 0) {
            return null;
        }
        // TODO: localize
        if (time < 10 * MINUTE_MILLIS) {
            return "Few minutes";
        } else if (time < 50 * MINUTE_MILLIS) {
            return "Approx " + time / MINUTE_MILLIS + " minutes";
        } else if (time < 75 * MINUTE_MILLIS) {
            return "Approx an hour";
        } else if (time < 135 * MINUTE_MILLIS) {
            return "Approx two hour";
        } else if (time < 24 * HOUR_MILLIS) {
            return "Approx " + time / HOUR_MILLIS + " hours";
        } else if (time < 48 * HOUR_MILLIS) {
            return "More than a day";
        } else {
            return "Approx " + time / DAY_MILLIS + " days";
        }
    }

    public static String getTimeInAgo(long time) {

        if (time == 0) {
            return null;
        }
        // TODO: localize
        if (time < MINUTE_MILLIS) {
            return time / 1000 + " seconds ago";
        } else if (time < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (time < 50 * MINUTE_MILLIS) {
            return time / MINUTE_MILLIS + " minutes ago";
        } else if (time < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (time < 24 * HOUR_MILLIS) {
            return time / HOUR_MILLIS + " hours ago";
        } else if (time < 48 * HOUR_MILLIS) {
            return "Tomorrow";
        } else {
            return time / DAY_MILLIS + " days ago";
        }
    }
}
