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
    CAT("CAT", "CT Scan"),
    ECH("ECH", "ECHO"),
    ECG("ECG", "ECG"),
    EEG("EEG", "EEG"),
    MRI("MRI", "MRI"),
    SON("SON", "Sonography"),
    STR("STR", "Stress"),
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

    public static Map<String, String> asMap() {
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

    @Override
    public String toString() {
        return description;
    }
}
