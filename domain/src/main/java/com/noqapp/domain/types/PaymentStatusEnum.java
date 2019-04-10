package com.noqapp.domain.types;

/**
 * hitender
 * 2019-02-28 14:53
 */
public enum PaymentStatusEnum {
    PP("PP", "Pending Payment"),
    MP("MP", "Multiple Payment"),
    PA("PA", "Paid"),
    PF("PF", "Payment Fail"),
    FP("FP", "Flagged Payment"),
    PC("PC", "Payment Cancelled"),
    PR("PR", "Payment Refunded");

    private final String description;
    private final String name;

    PaymentStatusEnum(String name, String description) {
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
