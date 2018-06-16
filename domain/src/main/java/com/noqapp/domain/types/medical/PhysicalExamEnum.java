package com.noqapp.domain.types.medical;

/**
 * hitender
 * 6/16/18 6:28 PM
 */
public enum PhysicalExamEnum {
    PL("PL", "Pluse"),
    BP("BP", "Blood Pressure"),
    WT("WT", "Weight");

    private final String description;
    private final String name;

    PhysicalExamEnum(String name, String description) {
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
