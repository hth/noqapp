package com.token.utils;

import java.util.regex.Pattern;

/**
 * User: hitender
 * Date: 11/25/16 10:06 AM
 */
public class Constants {

    public static final Pattern AGE_RANGE = Pattern.compile("^(\\d?[0-9]|[0-9])?(-\\d?[0-9]|[0-9])");

    private Constants() {
    }
}

