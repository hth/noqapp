package com.noqapp.domain.types;

/**
 * hitender
 * 6/14/18 1:49 PM
 */
public enum MedicationTypeEnum {
    TA("TA", "Tablet"),
    IN("IN", "Injection"),
    CA("CA", "Capsule"),
    SY("SY", "Syrup");

    private final String name;
    private final String description;

    MedicationTypeEnum(String name, String description) {
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
