package com.noqapp.domain.types;

/**
 * User: hitender
 * Date: 11/01/17 5:04 PM
 */
public enum BillingStatusEnum {
    C("C", "Current"),
    D("D", "Delinquent");

    private final String description;
    private final String name;

    BillingStatusEnum(String name, String description) {
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
