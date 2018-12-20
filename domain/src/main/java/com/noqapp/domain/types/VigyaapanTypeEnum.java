package com.noqapp.domain.types;

/**
 * hitender
 * 2018-12-20 13:07
 */
public enum VigyaapanTypeEnum {
    PP("PP", "Professional Profile"),
    MV("MV", "Merchant Vigyaapan"),
    DV("DV", "Department of Vigyaapan"),
    GI("GI", "General Information");

    private final String description;
    private final String name;

    VigyaapanTypeEnum(String name, String description) {
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
