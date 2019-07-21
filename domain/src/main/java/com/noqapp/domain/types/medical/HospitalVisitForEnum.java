package com.noqapp.domain.types.medical;

/**
 * User: hitender
 * Date: 2019-07-20 10:31
 */
public enum HospitalVisitForEnum {
    IMU("IMU", "Immunization"),
    VAC("VAC", "Vaccination");

    private final String description;
    private final String name;

    HospitalVisitForEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return description;
    }
}
