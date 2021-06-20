package com.noqapp.domain.types;

/**
 * Limit post capability based on traits listed here.
 * hitender
 * 6/19/21 4:09 AM
 */
public enum PersonalityTraitsEnum {
    NTW("NTW", "Not Trust Worthy");

    private final String name;
    private final String description;

    PersonalityTraitsEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return description;
    }
}
