package com.noqapp.domain.types;

/**
 * hitender
 * 2/24/18 10:23 AM
 */
public enum MedicationWithFoodEnum {
    BF("BF", "Before Food"),
    AF("AF", "After Food");

    private final String name;
    private final String description;

    MedicationWithFoodEnum(String name, String description) {
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
