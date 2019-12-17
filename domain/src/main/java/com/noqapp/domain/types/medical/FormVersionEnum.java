package com.noqapp.domain.types.medical;

/**
 * hitender
 * 7/24/18 10:51 AM
 */
public enum FormVersionEnum {
    /* Format of four letters, A1 to A9 and then B1 to B9 and so on. */
    MFD1("MFD1", "Dynamic Option", "Since 2019", "Exp 2021"),
    MFS1("MFS1", "Scribble", "Since 2019", "Exp 2021");

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
