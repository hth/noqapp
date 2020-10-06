package com.noqapp.domain.types;

/**
 * Help us track where the token was generated from.
 *
 * hitender
 * 1/22/18 10:43 PM
 */
public enum TokenServiceEnum {
    C("C", "Client"),
    M("M", "Merchant"),
    W("W", "Web"),
    S("S", "System");

    private final String name;
    private final String description;

    TokenServiceEnum(String name, String description) {
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
