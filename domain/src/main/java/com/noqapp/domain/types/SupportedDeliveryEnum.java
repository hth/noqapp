package com.noqapp.domain.types;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Store accepted delivery.
 * hitender
 * 11/7/20 9:53 AM
 */
public enum SupportedDeliveryEnum {
    HOM("HOM", "Home Delivery"),
    PIK("PIK", "Pick-up");

    private final String name;
    private final String description;

    SupportedDeliveryEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static Set<SupportedDeliveryEnum> all() {
        return new LinkedHashSet<>() {{
            add(HOM);
            add(PIK);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
