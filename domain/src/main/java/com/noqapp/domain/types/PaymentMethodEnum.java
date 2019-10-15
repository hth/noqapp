package com.noqapp.domain.types;

/**
 * User: hitender
 * Date: 10/15/19 11:09 PM
 */
public enum PaymentMethodEnum {
    CA("CA", "Cash", "Cash"),
    EL("EL", "Electronic", "Online");

    private final String name;
    private final String description;
    private final String friendlyDescription;

    PaymentMethodEnum(String name, String description, String friendlyDescription) {
        this.name = name;
        this.description = description;
        this.friendlyDescription = friendlyDescription;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFriendlyDescription() {
        return friendlyDescription;
    }

    @Override
    public String toString() {
        return description;
    }
}
