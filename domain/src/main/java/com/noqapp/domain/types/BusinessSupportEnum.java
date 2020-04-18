package com.noqapp.domain.types;

/**
 * hitender
 * 4/17/20 9:59 AM
 */
public enum BusinessSupportEnum {
    OD("OD", "Order"),
    OQ("OQ", "Order but Queue"),
    QQ("QQ", "Queue");

    private String name;
    private String description;

    BusinessSupportEnum(String name, String description) {
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
