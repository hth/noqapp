package com.noqapp.domain.types.medical;

/**
 * hitender
 * 5/30/18 12:04 AM
 */
public enum BloodTypeEnum {

    AP("AP", "A+"),
    AN("AN", "A-"),
    BP("BP", "B+"),
    BN("BN", "B-"),
    OP("OP", "O+"),
    ON("ON", "O-"),
    ABP("ABP", "AB+"),
    ABN("ABN", "AB-");

    private final String description;
    private final String name;

    BloodTypeEnum(String name, String description) {
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
