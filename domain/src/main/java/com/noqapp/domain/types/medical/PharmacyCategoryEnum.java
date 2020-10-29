package com.noqapp.domain.types.medical;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * hitender
 * 8/18/18 2:34 PM
 */
public enum PharmacyCategoryEnum {
    CA("CA", "Capsule"),
    CR("CR", "Cream"),
    IH("IH", "Inhaler"),
    IJ("IJ", "Injection"),
    LO("LO", "Lotion"),
    PW("PW", "Powder"),
    SY("SY", "Syrup"),
    TA("TA", "Tablet");

    private final String description;
    private final String name;

    PharmacyCategoryEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static List<PharmacyCategoryEnum> asList() {
        PharmacyCategoryEnum[] all = PharmacyCategoryEnum.values();
        return Arrays.asList(all);
    }

    public static List<String> asListOfDescription() {
        List<String> a = new LinkedList<>();
        for (PharmacyCategoryEnum pharmacyCategory : PharmacyCategoryEnum.values()) {
            a.add(pharmacyCategory.description);
        }

        return a;
    }

    public static Map<String, String> asMapWithNameAsKey() {
        return new LinkedHashMap<>() {{
            put(CA.name, CA.description);
            put(CR.name, CR.description);
            put(IH.name, IH.description);
            put(IJ.name, IJ.description);
            put(LO.name, LO.description);
            put(PW.name, PW.description);
            put(SY.name, SY.description);
            put(TA.name, TA.description);
        }};
    }

    public static Map<String, String> asMapWithDescriptionAsKey() {
        return new LinkedHashMap<>() {{
            put(CA.description, CA.name);
            put(CR.description, CR.name);
            put(IH.description, IH.name);
            put(IJ.description, IJ.name);
            put(LO.description, LO.name);
            put(PW.description, PW.name);
            put(SY.description, SY.name);
            put(TA.description, TA.name);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
