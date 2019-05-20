package com.noqapp.domain.types;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-05-18 14:14
 */
public enum AdvertisementDisplayEnum {
    TV("TV", "TV"),
    MC("MC", "Mobile Client"),
    MM("MM", "Mobile Merchant"),
    WW("WW", "World Wide Web");

    private final String description;
    private final String name;

    AdvertisementDisplayEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static List<AdvertisementDisplayEnum> asList() {
        AdvertisementDisplayEnum[] all = AdvertisementDisplayEnum.values();
        return Arrays.asList(all);
    }

    public static EnumSet<AdvertisementDisplayEnum> FOR_BUSINESS = EnumSet.of(TV, MC);

    @Override
    public String toString() {
        return description;
    }
}
