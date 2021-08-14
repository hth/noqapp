package com.noqapp.common.utils;

import okhttp3.MediaType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * User: hitender
 * Date: 11/25/16 10:06 AM
 */
public class Constants {

    public static final String TOPICS = "/topics/";
    public static final Pattern AGE_RANGE = Pattern.compile("^(\\d?[0-9]|[0-9])?(-\\d?[0-9]|[0-9])");
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String MAX_Q_SEARCH_DISTANCE = "4000"; //Close to 93 Miles or 150 KM
    public static final String MAX_Q_SEARCH_DISTANCE_WITH_UNITS = MAX_Q_SEARCH_DISTANCE + "km"; //Close to 93 Miles
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String UNDER_SCORE = "_";
    public static final Charset CHAR_SET_UTF8 = StandardCharsets.UTF_8;
    public static final Pattern WORD_PATTERN = Pattern.compile("^[A-Za-z .-]+");
    public static final Pattern TIME_SLOT_PATTERN = Pattern.compile("^[0-9:\\s-]+$");
    public static final String WORD_PATTERN_TEXT = " words 'A-to-Z', spaces ' ' and/or dots/dash '.', '-";
    public static final int PRIME_73 = 73;
    public static final String appendPrefix = "Q";
    public static final String DIRTY = "Y";
    public static final int PREVENT_JOINING_BEFORE_CLOSING = 30;    //Minutes
    public static final int MINUTES_01 = 1;
    public static final int MINUTES_05 = MINUTES_01 * 5;
    public static final int MINUTES_15 = MINUTES_05 * 3;            //Minutes
    public static final int MINUTES_30 = MINUTES_15 * 2;            //Minutes
    public static final int MINUTES_45 = MINUTES_15 * 3;            //Minutes
    public static final int MINUTES_60 = MINUTES_30 * 2;            //Minutes
    public static final int MINUTES_59 = MINUTES_60 - 1;            //Minutes
    public static final int MINUTES_IN_MILLISECOND = 60_000;        //1 minutes in milliseconds
    public static final int MINUTES_2_IN_MILLISECOND = MINUTES_IN_MILLISECOND * 2;
    public static final int HUNDRED_KMS_IN_METERS = 100 * 1000;
    public static final String MISSING_QUEUE_IDS = "missingQids";
    public static final double TEN_METERS_IN_KILOMETER = 0.01;
    public static final double FLEX_APPOINTMENT_LOAD_FACTOR = 0.4;
    public static final int DAYS_15 = 15;

    public static final Set<String> UNSUPPORTED_DEVICE_IN_FUTURE = new HashSet<>() {{
        add("5.1");
        add("5.1.1");
    }};

    /* Used in mobile. */
    @SuppressWarnings("unused")
    public static final List<String> SUGGESTED_SEARCH = new LinkedList<>() {{
        add("URC");
        add("Bani Camp");
        add("AFSSR");
        add("URC 54 ASP");
        add("URC CHAF Bangalore");
        add("URC Adampur");
    }};

    private Constants() {
    }
}

