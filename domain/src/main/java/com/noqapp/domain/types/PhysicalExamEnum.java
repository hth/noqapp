package com.noqapp.domain.types;

/**
 * hitender
 * 3/7/18 9:56 PM
 */
public enum PhysicalExamEnum {
    P("P", "Pluse"),
    B("B", "B.P"),
    W("W", "Weight");

    private final String name;
    private final String description;

    PhysicalExamEnum(String name, String description) {
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
