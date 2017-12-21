package com.noqapp.common.utils;

import okhttp3.MediaType;

import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * User: hitender
 * Date: 11/25/16 10:06 AM
 */
public class Constants {

    public static final Pattern AGE_RANGE = Pattern.compile("^(\\d?[0-9]|[0-9])?(-\\d?[0-9]|[0-9])");
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String MAX_Q_SEARCH_DISTANCE = "150km"; //Close to 93 Miles
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final Charset CHAR_SET_UTF8 = Charset.forName("UTF-8");
    public static final Pattern WORD_PATTERN = Pattern.compile("^[A-Za-z .]+");
    public static final String WORD_PATTERN_TEXT = " words 'A-Za-z', spaces ' ' and/or dots '.'";

    private Constants() {
    }
}

