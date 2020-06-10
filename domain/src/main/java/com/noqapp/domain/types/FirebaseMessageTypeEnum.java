package com.noqapp.domain.types;

/**
 * These messages are agnostic to any client type or business type.
 * User: hitender
 * Date: 3/7/17 10:58 PM
 */
public enum FirebaseMessageTypeEnum {
    C("C", "Client"),
    M("M", "Merchant"),
    P("P", "Personal");

    private final String description;
    private final String name;

    FirebaseMessageTypeEnum(String name, String description) {
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
