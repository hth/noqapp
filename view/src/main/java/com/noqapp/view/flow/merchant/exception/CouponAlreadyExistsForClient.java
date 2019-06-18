package com.noqapp.view.flow.merchant.exception;

/**
 * User: hitender
 * Date: 2019-06-18 02:32
 */
public class CouponAlreadyExistsForClient extends RuntimeException {
    public CouponAlreadyExistsForClient(String message) {
        super(message);
    }

    public CouponAlreadyExistsForClient(String message, Throwable throwable) {
        super(message, throwable);
    }
}
