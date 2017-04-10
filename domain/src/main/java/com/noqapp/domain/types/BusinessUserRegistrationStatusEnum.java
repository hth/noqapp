package com.noqapp.domain.types;

/**
 * User: hitender
 * Date: 11/23/16 4:37 PM
 */
public enum BusinessUserRegistrationStatusEnum {
    /** Set by system. */
    I("I", "In Complete"),
    C("C", "Complete"),

    /** Set by reviewer. */
    N("N", "Not Valid"),
    V("V", "Valid");

    private final String description;
    private final String name;

    BusinessUserRegistrationStatusEnum(String name, String description) {
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

