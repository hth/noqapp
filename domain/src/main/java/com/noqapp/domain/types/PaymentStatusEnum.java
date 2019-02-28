package com.noqapp.domain.types;

/**
 * hitender
 * 2019-02-28 14:53
 */
public enum PaymentStatusEnum {

    UP("UP", "Un-Paid"),
    PA("PA", "Paid"),
    PF("PF", "Payment Fail");

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
