package com.noqapp.domain.types;

/**
 * User: hitender
 * Date: 11/25/16 9:42 AM
 */
public enum NotificationStateEnum {
    S("S", "Success"),
    F("F", "Failure");

    private final String name;
    private final String description;

    NotificationStateEnum(String name, String description) {
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
