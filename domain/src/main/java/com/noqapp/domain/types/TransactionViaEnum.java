package com.noqapp.domain.types;

/**
 * hitender
 * 2019-03-20 14:35
 */
public enum TransactionViaEnum {
    I("I", "Internal", "Within NoQueue"),
    E("E", "External", "Outside of NoQueue"),
    U("U", "Unknown", "N/A");

    private final String name;
    private final String description;
    private final String friendlyDescription;

    TransactionViaEnum(String name, String description, String friendlyDescription) {
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
