package com.noqapp.domain.types;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * hitender
 * 3/27/18 10:08 AM
 */
public enum AmenityEnum {
    AC("AC", "Air Condition"),
    FW("FW", "Free Wifi"),
    FP("FP", "Free Parking"),
    WA("WA", "Wheelchair Access"),
    ME("ME", "Meeting Room");

    public static EnumSet<AmenityEnum> ALL = EnumSet.of(AC, FW, FP, WA, ME);
    public static EnumSet<AmenityEnum> GROCERY = EnumSet.of(AC, FW, FP, WA);

    private final String description;
    private final String name;

    AmenityEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static List<AmenityEnum> asList() {
        AmenityEnum[] all = AmenityEnum.values();
        return Arrays.asList(all);
    }

    @Override
    public String toString() {
        return description;
    }
}
