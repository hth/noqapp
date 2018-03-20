package com.noqapp.domain.types;

/**
 * hitender
 * 3/5/18 3:06 AM
 */
public enum MedicationRouteEnum {
    OR("OR", "Oral"),
    IM("IM", "After Food"),
    IV("IV", "Intravenous"),
    SC("SC", "After Food");

    private final String name;
    private final String description;

    MedicationRouteEnum(String name, String description) {
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
