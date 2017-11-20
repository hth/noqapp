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
        bd = bd.setScale(scale, BigDecimal.ROUND_HALF_UP);
        LOG.debug("Float scaling before f={} after f={}", f, bd.floatValue());
        return bd.floatValue();
    }

    public static float roundFloat(float f) {
        return roundFloat(f, 2);
    }
}
