package com.noqapp.common.utils;

/**
 * hitender
 * 6/3/20 8:50 PM
 */
public class GetTimeAgoUtils {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time) {

        if (time == 0) {
            return null;
        }
        // TODO: localize
        final long diff = time;
        if (diff < MINUTE_MILLIS) {
            return diff / 1000 + " seconds";
        } else if (diff < 10 * MINUTE_MILLIS) {
            return "Couple of minutes";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "Approx " + diff / MINUTE_MILLIS + " minutes";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "Approx an hour";
        } else if (diff < 24 * HOUR_MILLIS) {
            return "Approx " + diff / HOUR_MILLIS + " hours";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "More than a day";
        } else {
            return "Approx " + diff / DAY_MILLIS + " days";
        }
    }

    public static String getTimeInAgo(long time) {

        if (time == 0) {
            return null;
        }
        // TODO: localize
        final long diff = time;
        if (diff < MINUTE_MILLIS) {
            return diff / 1000 + " seconds ago";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Tomorrow";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }
}
