package com.noqapp.domain.types;

/**
 * hitender
 * 2019-03-20 14:35
 */
public enum TransactionViaEnum {
    I("I", "Internal"),
    E("E", "External"),
    U("U", "Unknown");

    private final String name;
    private final String description;

    TransactionViaEnum(String name, String description) {
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
