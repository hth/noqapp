package com.noqapp.service.exceptions;

/**
 * Product Not Found exception.
 * hitender
 * 2019-03-19 16:02
 */
public class PurchaseOrderProductNFException extends RuntimeException {

    public PurchaseOrderProductNFException(String message) {
        super(message);
    }

    public PurchaseOrderProductNFException(String message, Throwable cause) {
        super(message, cause);
    }
}
