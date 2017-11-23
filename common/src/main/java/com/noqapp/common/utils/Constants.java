package com.noqapp.common.utils;

import okhttp3.MediaType;

import java.util.regex.Pattern;

/**
 * User: hitender
 * Date: 11/25/16 10:06 AM
 */
public class Constants {

    public static final Pattern AGE_RANGE = Pattern.compile("^(\\d?[0-9]|[0-9])?(-\\d?[0-9]|[0-9])");
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String MAX_Q_SEARCH_DISTANCE = "150km"; //Close to 93 Miles

    private Constants() {
    }
}

