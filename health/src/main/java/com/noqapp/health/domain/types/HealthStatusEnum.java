package com.noqapp.health.domain.types;

/**
 * User: hitender
 * Date: 11/06/2017 2:12 PM
 */
public enum HealthStatusEnum {
    G("G", "Good"),
    B("B", "Bad");

    private final String description;
    private final String name;

    HealthStatusEnum(String name, String description) {
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
