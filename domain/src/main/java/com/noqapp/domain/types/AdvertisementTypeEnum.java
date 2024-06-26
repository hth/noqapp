package com.noqapp.domain.types;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * hitender
 * 2018-12-20 13:07
 */
public enum AdvertisementTypeEnum {
    PP("PP", "Professional Profile"),
    MA("MA", "Advertisement"),
    GI("GI", "General Information");

    private final String description;
    private final String name;

    AdvertisementTypeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static List<AdvertisementTypeEnum> asList() {
        AdvertisementTypeEnum[] all = AdvertisementTypeEnum.values();
        return Arrays.asList(all);
    }

    public static EnumSet<AdvertisementTypeEnum> FOR_BUSINESS = EnumSet.of(MA, GI);

    @Override
    public String toString() {
        return description;
    }
}
