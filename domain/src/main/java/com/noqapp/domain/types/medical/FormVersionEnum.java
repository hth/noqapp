package com.noqapp.domain.types.medical;

/**
 * hitender
 * 7/24/18 10:51 AM
 */
public enum FormVersionEnum {
    GEV1("GEV1", "General Form v1", "Since 2018", "Exp 2019"),
    GEV2("GEV2", "General Form v2", "Since 2018", "Exp 2019");

    private final String name;
    private final String description;
    private final String formSince;
    private final String formExpiration;

    FormVersionEnum(String name, String description, String formSince, String formExpiration) {
        this.name = name;
        this.description = description;
        this.formSince = formSince;
        this.formExpiration = formExpiration;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFormSince() {
        return formSince;
    }

    public String getFormExpiration() {
        return formExpiration;
    }

    @Override
    public String toString() {
        return description;
    }
}
