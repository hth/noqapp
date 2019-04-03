package com.noqapp.domain.types;

/**
 * hitender
 * 2019-03-13 11:45
 */
public enum ServicePaymentEnum {
    R("R", "Required"),
    O("O", "Optional");

    private final String name;
    private final String description;

    ServicePaymentEnum(String name, String description) {
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
