package com.noqapp.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * User: hitender
 * Date: 9/24/17 10:44 PM
 */
public class MathUtil {
    private static final Logger LOG = LoggerFactory.getLogger(MathUtil.class);

    static float roundFloat(float f, int scale) {
        BigDecimal bd = new BigDecimal(Float.toString(f));
        return bd.setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static float roundFloat(float f) {
        return roundFloat(f, 2);
    }
}
