package com.noqapp.domain.types;

import java.util.EnumSet;

/**
 * User: hitender
 * Date: 2019-05-20 14:11
 */
public enum AdvertisementViewerTypeEnum {
    WTC("WTC", "With Terms And Conditions"),
    JBA("JBA", "Just Banner"); //Allways need Image check add

    private final String description;
    private final String name;

    AdvertisementViewerTypeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static EnumSet<AdvertisementViewerTypeEnum> SUPPORTED = EnumSet.of(WTC, JBA);

    @Override
    public String toString() {
        return description;
    }
}
