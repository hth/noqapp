package com.noqapp.domain.types;

/**
 * hitender
 * 12/18/17 1:54 AM
 */
public enum QuestionnaireTypeEnum {
    B("B", "Before Join"),
    A("A", "After Join"), //Rare, might not support
    S("S", "After Service");

    private final String name;
    private final String description;

    QuestionnaireTypeEnum(String name, String description) {
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
