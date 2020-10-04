package com.noqapp.view.flow.merchant.exception;

/**
 * hitender
 * 10/3/20 8:04 PM
 */
public class CouponGlobalAlreadyExistsException extends RuntimeException {
    public CouponGlobalAlreadyExistsException(String message) {
        super(message);
    }

    public CouponGlobalAlreadyExistsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
