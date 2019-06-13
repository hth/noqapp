package com.noqapp.service.exceptions;

/**
 * User: hitender
 * Date: 2019-06-12 14:50
 */
public class CouponCannotApplyException extends RuntimeException {

    public CouponCannotApplyException(String message) {
        super(message);
    }

    public CouponCannotApplyException(String message, Throwable cause) {
        super(message, cause);
    }
}
