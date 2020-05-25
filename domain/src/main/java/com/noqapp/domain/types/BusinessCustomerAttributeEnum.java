package com.noqapp.domain.types;

/**
 * hitender
 * 5/24/20 3:16 AM
 */
public enum BusinessCustomerAttributeEnum {
    AP("AP", "Approved"),
    RJ("RJ", "Reject"),
    GY("GY", "Grocery"),
    LQ("LQ", "Liquor");

    private final String description;
    private final String name;

    BusinessCustomerAttributeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return description;
    }
}
