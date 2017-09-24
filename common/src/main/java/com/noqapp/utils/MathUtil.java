package com.noqapp.utils;

import java.math.BigDecimal;

/**
 * User: hitender
 * Date: 9/24/17 10:44 PM
 */
public class MathUtil {

    static float roundFloat(float f, int scale) {
        BigDecimal bd = new BigDecimal(Float.toString(f));
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static float roundFloat(float f) {
        return roundFloat(f, 2);
    }
}
