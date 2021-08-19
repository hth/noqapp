package com.noqapp.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * User: hitender
 * Date: 9/24/17 10:44 PM
 */
public class MathUtil {
    private static final Logger LOG = LoggerFactory.getLogger(MathUtil.class);

    static float roundFloat(float f, int scale) {
        BigDecimal bd = new BigDecimal(Float.toString(f));
        return bd.setScale(scale, RoundingMode.HALF_UP).floatValue();
    }

    public static float roundFloat(float f) {
        return roundFloat(f, 2);
    }

    public static String displayPrice(String number) {
        return new BigDecimal(number).movePointLeft(2).toString();
    }

    public static String displayPrice(int number) {
        return new BigDecimal(number).movePointLeft(2).toString();
    }

    public static String correctPriceForDB(final BigDecimal number) {
        return number.movePointRight(2).toString();
    }

    public static String correctPriceForDB(String number) {
        return correctPriceForDB(new BigDecimal(number));
    }
}
