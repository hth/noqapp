package com.noqapp.domain.types;

/**
 * hitender
 * 3/27/18 12:35 PM
 */
public enum FacilityEnum {
    EM("EM", "Emergency Medicine"),
    IC("IC", "High End ICU"),
    IU("IU", "ICCU"),
    NI("NI", "NICU"),
    RA("RA", "Radiology"),
    MP("MP", "Modern Pathology"),
    IF("IF", "Laparoscopic Surgery"),
    LS("LS", "General Surgery"),
    GS("GS", "Plastic & Cosmetic Surgery"),
    CS("CS", "Psychiatry"),
    PY("PY", "Sexology"),
    SX("SX", "Gynaecology & Obstetrics"),
    GY("GY", "Dentistry"),
    DE("DE", "Cashless Treatment"),
    AH("AH", "Health Checkup Plans"),
    SO("SO", "Sunday OPD");

    private final String description;
    private final String name;

    FacilityEnum(String name, String description) {
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
