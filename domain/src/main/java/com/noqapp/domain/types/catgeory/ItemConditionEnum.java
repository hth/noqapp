package com.noqapp.domain.types.catgeory;

import java.util.EnumSet;

/**
 * hitender
 * 2/24/21 4:37 PM
 */
public enum ItemConditionEnum {
    G("G", "Good"),
    P("P", "Poor"),
    V("V", "Very Good");

    private final String description;
    private final String name;

    ItemConditionEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static EnumSet<ItemConditionEnum> itemConditionTypes = EnumSet.of(G, P, V);

    @Override
    public String toString() {
        return description;
    }
}
