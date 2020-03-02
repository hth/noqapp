package com.noqapp.domain.types;

import com.noqapp.domain.annotation.Mobile;

/**
 * App flavor to distinguish between different apps.
 * hitender
 * 7/17/18 11:41 AM
 */
@Mobile
public enum AppFlavorEnum {
    NQCL("NQCL", "NoQueue Client"),
    NQCH("NQCH", "NoQueue Client Hospital"),
    NQMS("NQMS", "NoQueue Merchant Store"),
    NQMH("NQMH", "NoQueue Merchant HealthCare"),
    NQMT("NQMT", "NoQueue Merchant TV"),
    NQMI("NQMI", "NoQueue Merchant Inventory"),
    NQIH("NQIH", "NoQueue Instant Health");

    private String name;
    private String description;

    AppFlavorEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
