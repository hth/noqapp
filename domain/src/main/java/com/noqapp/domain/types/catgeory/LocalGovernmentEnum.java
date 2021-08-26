package com.noqapp.domain.types.catgeory;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 8/25/21 8:50 PM
 */
public enum LocalGovernmentEnum {
    ADM("ADM", "Administrator"),
    OFC("OFC", "Officers");

    private final String name;
    private final String description;

    LocalGovernmentEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static EnumSet<LocalGovernmentEnum> localGovernments = EnumSet.of(ADM, OFC);

    public static List<LocalGovernmentEnum> ordered() {
        return new LinkedList<>() {{
            add(ADM);
            add(OFC);
        }};
    }
}