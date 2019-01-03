package com.noqapp.domain.types.medical;

/**
 * hitender
 * 2019-01-03 13:29
 */
public enum MedicationIntakeEnum {
    BF("BF", "Before Food"),
    AF("AF", "After Food"),
    ES("ES", "Empty Stomach");

    private final String description;
    private final String name;

    MedicationIntakeEnum(String name, String description) {
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
