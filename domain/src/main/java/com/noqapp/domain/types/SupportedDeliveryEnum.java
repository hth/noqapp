package com.noqapp.domain.types;

/**
 * Store accepted delivery.
 * hitender
 * 11/7/20 9:53 AM
 */
public enum SupportedDeliveryEnum {
    HOM("HOM", "Home Delivery"),
    PIK("PIK", "Pick-up");

    private final String name;
    private final String description;

    SupportedDeliveryEnum(String name, String description) {
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
