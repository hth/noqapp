package com.noqapp.domain.types;

/**
 * hitender
 * 8/21/20 6:17 PM
 */
public enum MessageCodeEnum {
    SMTS("SMTS", 1, "SMS Time Slot"),
    SMEW("SMEW", 1, "SMS Estimated Wait");

    private final String name;
    private final int version;
    private final String description;

    MessageCodeEnum(String name, int version, String description) {
        this.name = name;
        this.description = description;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
