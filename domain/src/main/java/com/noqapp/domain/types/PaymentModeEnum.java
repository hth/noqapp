package com.noqapp.domain.types;

/**
 * hitender
 * 3/27/18 10:03 AM
 */
public enum PaymentModeEnum {
    CA("CA", "Cash"),
    CC("CC", "Credit Card"),
    DC("DC", "Debit Card"),
    PT("PT", "Paytm"),
    UP("UP", "UPI");

    private final String description;
    private final String name;

    PaymentModeEnum(String name, String description) {
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
