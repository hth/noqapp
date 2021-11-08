package com.noqapp.domain.types.catgeory;

import java.util.EnumSet;

/**
 * hitender
 * 11/7/21 7:47 AM
 */
public enum MarketplaceRejectReasonEnum {
    ADIM("ADIM", "Add Image"),
    ADMD("ADMD", "Add More Details"),
    URIS("URIS", "User Reported Issue");

    private final String name;
    private final String description;

    MarketplaceRejectReasonEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static EnumSet<MarketplaceRejectReasonEnum> marketplaceRejectReasons = EnumSet.of(ADIM, ADMD, URIS);
}
