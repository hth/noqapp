package com.noqapp.domain.types;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return Stream.of(AmenityEnum.values())
            .sorted(Comparator.comparing(AmenityEnum::getDescription))
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return description;
    }
}
