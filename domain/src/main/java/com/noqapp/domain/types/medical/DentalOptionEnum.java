package com.noqapp.domain.types.medical;

/**
 * User: hitender
 * Date: 2019-08-17 09:17
 */
public enum DentalOptionEnum {
    NOR("NOR", "Normal"),
    CAV("CAV", "Cavity"),
    IMP("IMP", "Implant");

    private final String description;
    private final String name;

    DentalOptionEnum(String name, String description) {
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
