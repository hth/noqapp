package com.noqapp.domain.types;

/**
 * hitender
 * 3/15/18 4:21 PM
 */
public enum RadiologyEnum {
    XRay_RGU("XRay_RGU", "XRay RGU"),
    Sono_Pelvis("Sono_Pelvis", "Sono Pelvis");

    private final String name;
    private final String description;

    RadiologyEnum(String name, String description) {
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
