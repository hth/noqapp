package com.noqapp.domain.types.medical;

/**
 * hitender
 * 8/18/18 2:34 PM
 */
public enum MedicineTypeEnum {
    CA("CA", "Capsule"),
    CR("CR", "Cream"),
    IH("IH", "Inhaler"),
    IJ("IJ", "Injection"),
    PW("PW", "Powder"),
    SY("SY", "Syrup"),
    TA("TA", "Tablet");

    private final String description;
    private final String name;

    MedicineTypeEnum(String name, String description) {
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
