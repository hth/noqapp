package com.noqapp.domain.types;

/**
 * User: hitender
 * Date: 11/25/16 9:43 AM
 */
public enum NotificationMarkerEnum {
    S("S", "Simple Notify"),
    P("p", "Push Notify"),
    I("I", "Ignore Notifying");

    private final String description;
    private final String name;

    NotificationMarkerEnum(String name, String description) {
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
