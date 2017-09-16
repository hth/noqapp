package com.noqapp.domain.types;

/**
 * User: hitender
 * Date: 9/16/17 12:24 AM
 */
public enum AddressOriginEnum {
    S("S", "Self"),
    G("G", "Google");

    private final String name;
    private final String description;

    AddressOriginEnum(String name, String description) {
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
