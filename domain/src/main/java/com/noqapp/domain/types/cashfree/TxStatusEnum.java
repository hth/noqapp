package com.noqapp.domain.types.cashfree;

/**
 * hitender
 * 2019-03-04 08:10
 */
public enum TxStatusEnum {
    SUCCESS("SUCCESS", "Successful Payment"),
    FAILED("FAILED", "Payment Failed"),
    PENDING("PENDING", "Pending Payment"),
    CANCELLED("CANCELLED", "Payment cancelled by user"),
    FLAGGED("FLAGGED", "Payment successful but kept on hold by risk system");

    private final String name;
    private final String description;

    TxStatusEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
