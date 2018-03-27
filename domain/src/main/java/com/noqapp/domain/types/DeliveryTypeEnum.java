package com.noqapp.domain.types;

/**
 * hitender
 * 3/27/18 10:04 AM
 */
public enum DeliveryTypeEnum {
    HD("HD", "Home Delivery"),
    TO("TO", "Takeaway");

    private final String description;
    private final String name;

    DeliveryTypeEnum(String name, String description) {
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
