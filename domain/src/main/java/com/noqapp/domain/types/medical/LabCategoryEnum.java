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
public enum LabCategoryEnum {
    MRI("MRI", "MRI"),
    SCAN("SCAN", "CT Scan"),
    SONO("SONO", "Sonography"),
    XRAY("XRAY", "X-ray"),
    PATH("PATH", "Pathology"),
    SPEC("SPEC", "Special Diagnostic");

    private final String description;
    private final String name;

    LabCategoryEnum(String name, String description) {
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
        for (LabCategoryEnum category : LabCategoryEnum.values()) {
            a.add(category.description);
        }

        return a;
    }

    @Deprecated
    public static Map<String, String> asMapWithNameAsKey() {
        return new LinkedHashMap<String, String>() {{
            put(SCAN.name, SCAN.description);
            put(SONO.name, SONO.description);
            put(XRAY.name, XRAY.description);
            put(MRI.name, MRI.description);
            put(SPEC.name, SPEC.description);
        }};
    }

    @Deprecated
    public static Map<String, String> asMapWithDescriptionAsKey() {
        return new LinkedHashMap<String, String>() {{
            put(SCAN.description, SCAN.name);
            put(SONO.description, SONO.name);
            put(XRAY.description, XRAY.name);
            put(MRI.description, MRI.name);
            put(SPEC.description, SPEC.name);
        }};
    }

    public static Map<String, String> asMapWithNameAsKey_Self(LabCategoryEnum radiologyCategory) {
        return new LinkedHashMap<String, String>() {{
            put(radiologyCategory.name, radiologyCategory.description);
        }};
    }

    public static Map<String, String> asMapWithDescriptionAsKey_Self(LabCategoryEnum radiologyCategory) {
        return new LinkedHashMap<String, String>() {{
            put(radiologyCategory.description, radiologyCategory.name);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
