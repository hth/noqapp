package com.noqapp.domain.types.medical;

/**
 * hitender
 * 6/25/18 6:38 PM
 */
public enum MedicalDepartmentEnum {
    DNT("DNT", "Dentist"),
    CRD("CRD", "Cardiologist"),
    GYN("GYN", "Gynaecology");

    private final String description;
    private final String name;

    MedicalDepartmentEnum(String name, String description) {
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
