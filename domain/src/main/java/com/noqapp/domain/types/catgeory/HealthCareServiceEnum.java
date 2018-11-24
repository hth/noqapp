package com.noqapp.domain.types.catgeory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
    PATH("PATH", "Pathology");

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
}
