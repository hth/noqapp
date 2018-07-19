package com.noqapp.domain.types.medical;

/**
 * hitender
 * 6/16/18 6:28 PM
 */
public enum PhysicalGeneralExamEnum {
    TE("TM", "Temperature"),
    PL("PL", "Pluse"),
    BP("BP", "Blood Pressure"),
    OX("OX", "O2 Scan"),
    WT("WT", "Weight");

    private final String description;
    private final String name;

    PhysicalGeneralExamEnum(String name, String description) {
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
