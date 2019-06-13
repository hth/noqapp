package com.noqapp.service.exceptions;

/**
 * User: hitender
 * Date: 2019-06-13 23:26
 */
public class CouponRemovalException extends RuntimeException {

    public CouponRemovalException(String message) {
        super(message);
    }

    public CouponRemovalException(String message, Throwable cause) {
        super(message, cause);
    }
}
