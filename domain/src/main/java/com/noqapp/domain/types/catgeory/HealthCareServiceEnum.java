package com.noqapp.domain.types.catgeory;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * hitender
 * 11/19/18 4:33 PM
 */
public enum HealthCareServiceEnum {
    MRI("MRI", "MRI"),
    SCAN("SCAN", "CT Scan"),
    SONO("SONO", "Sonography"),
    XRAY("XRAY", "X-ray"),
    PHYS("PHYS", "Physiotherapy"),
    PATH("PATH", "Pathology"),
    SPEC("SPEC", "Special Diagnostic");

    private final String description;
    private final String name;

    HealthCareServiceEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static List<HealthCareServiceEnum> asList() {
        HealthCareServiceEnum[] all = HealthCareServiceEnum.values();
        return Arrays.asList(all);
    }

    public static List<String> asListOfDescription() {
        List<String> a = new LinkedList<>();
        for (HealthCareServiceEnum healthCareService : HealthCareServiceEnum.values()) {
            a.add(healthCareService.description);
        }

        return a;
    }

    public static Map<String, String> asMapWithNameAsKey() {
        return new LinkedHashMap<>() {{
            put(MRI.name, MRI.description);
            put(SCAN.name, SCAN.description);
            put(SONO.name, SONO.description);
            put(XRAY.name, XRAY.description);
            put(PHYS.name, PHYS.description);
            put(PATH.name, PATH.description);
            put(SPEC.name, SPEC.description);
        }};
    }

    public static Map<String, String> asMapWithDescriptionAsKey() {
        return new LinkedHashMap<>() {{
            put(MRI.description, MRI.name);
            put(SCAN.description, SCAN.name);
            put(SONO.description, SONO.name);
            put(XRAY.description, XRAY.name);
            put(PHYS.description, PHYS.name);
            put(PATH.description, PATH.name);
            put(SPEC.description, SPEC.name);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
