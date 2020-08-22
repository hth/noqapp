package com.noqapp.domain.types;

/**
 * hitender
 * 8/21/20 6:17 PM
 */
public enum MessageCodeEnum {
    SMTS("SMTS", "SMS Time Slot"),
    SMEW("SMEW", "SMS Estimated Wait");

    private final String name;
    private final String description;

    MessageCodeEnum(String name, String description) {
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
