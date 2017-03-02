package com.token.domain.types;

/**
 * User: hitender
 * Date: 3/2/17 11:28 AM
 */
public enum QueueStatusEnum {
    S("S", "Start"),
    N("N", "Next"),
    D("D", "Done"),
    C("C", "Closed");

    private final String name;
    private final String description;

    QueueStatusEnum(String name, String description) {
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
