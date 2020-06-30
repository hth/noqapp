package com.noqapp.domain.types;

import java.util.EnumSet;

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
    SO("SO", "Sunday OPD"),

    /* For Grocery Stores */
    DEL("DEL", "Delivery"),
    PIK("PIK", "Pick Up"),

    /* For Restaurant Stores */
    FRS("FRS", "Fresh Food"),

    CAS("CAS", "Cash"),
    CRD("CRD", "Debit/Credit Cards"),
    FRW("FRW", "Free Drinking Water");

    public static EnumSet<FacilityEnum> DOCTOR_HOSPITAL = EnumSet.of(EM, IC, IU, NI, RA, MP, IF, LS, GS, CS, PY, SX, GY, DE, AH, SO);
    public static EnumSet<FacilityEnum> GROCERY = EnumSet.of(DEL, PIK, CAS, CRD);
    public static EnumSet<FacilityEnum> RESTAURANT = EnumSet.of(DEL, FRS, FRW);

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
