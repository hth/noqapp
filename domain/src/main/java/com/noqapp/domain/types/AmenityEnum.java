package com.noqapp.domain.types;

/**
 * hitender
 * 3/27/18 10:08 AM
 */
public enum AmenityEnum {

    AC("AC", "Air Condition"),
    FW("FW", "Free Wifi"),
    FP("FP", "Free Parking"),
    WA("WA", "Wheelchair Access"),
    ME("ME", "Meeting Room");

    private final String description;
    private final String name;

    AmenityEnum(String name, String description) {
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
