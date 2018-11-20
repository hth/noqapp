package com.noqapp.domain.types.medical;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * hitender
 * 11/10/18 6:39 PM
 */
public enum RadiologyCategoryEnum {
    CDU("CDU", "Color Doppler"),
    SON("SON", "Sonography"),

    ECH("ECH", "ECHO"),
    ECG("ECG", "ECG"),
    EEG("EEG", "EEG"),
    STR("STR", "Stress"),

    CAT("CAT", "CT Scan"),
    PET("PET", "PET Scan"),
    MRI("MRI", "MRI"),
    XRY("XRY", "X-Ray");

    private final String description;
    private final String name;

    RadiologyCategoryEnum(String name, String description) {
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
        for (RadiologyCategoryEnum radiologyCategory : RadiologyCategoryEnum.values()) {
            a.add(radiologyCategory.description);
        }

        return a;
    }

    public static Map<String, String> asMapWithNameAsKey() {
        return new LinkedHashMap<String, String>() {{
            put(CDU.name, CDU.description);
            put(CAT.name, CAT.description);
            put(ECH.name, ECH.description);
            put(ECG.name, ECG.description);
            put(EEG.name, EEG.description);
            put(MRI.name, MRI.description);
            put(SON.name, SON.description);
            put(STR.name, STR.description);
            put(XRY.name, XRY.description);
        }};
    }

    public static Map<String, String> asMapWithDescriptionAsKey() {
        return new LinkedHashMap<String, String>() {{
            put(CDU.description, CDU.name);
            put(CAT.description, CAT.name);
            put(ECH.description, ECH.name);
            put(ECG.description, ECG.name);
            put(EEG.description, EEG.name);
            put(MRI.description, MRI.name);
            put(SON.description, SON.name);
            put(STR.description, STR.name);
            put(XRY.description, XRY.name);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
