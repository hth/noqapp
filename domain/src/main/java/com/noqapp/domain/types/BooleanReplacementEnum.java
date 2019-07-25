package com.noqapp.domain.types;

/**
 * User: hitender
 * Date: 2019-07-22 11:07
 */
public enum BooleanReplacementEnum {
    S("S", "Skip", "#CD334E"),
    Y("Y", "Yes", "#19769f"),
    N("N", "No", "#dddddd");

    private final String description;
    private final String name;
    private final String color;

    BooleanReplacementEnum(String name, String description, String color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return description;
    }
}
