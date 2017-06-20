package com.noqapp.domain.types;

/**
 * User: hitender
 * Date: 3/2/17 11:28 AM
 */
public enum QueueStatusEnum {
    S("S", "Start"),
    R("R", "Re-Start"),
    N("N", "Next"),
    /* Pause is a status for Server, Pause applies to just server. Queue never pauses. */
    P("P", "Pause"),
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
