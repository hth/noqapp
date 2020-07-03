package com.noqapp.domain.types;

/**
 * User: hitender
 * Date: 3/2/17 11:28 AM
 */
public enum QueueStatusEnum {
    S("S", "Start"),
    R("R", "Re-Start"),
    N("N", "Next"),
    /* Pause is a status for Server/Merchant, Pause applies to just Server/Merchant. Queue never pauses. */
    P("P", "Pause"),
    D("D", "Done"),
    C("C", "Closed"),

    /* These should not be used for FCM messages. These needs to be fixed in a separate enum. */
    /* B is when user joins with a different time on mobile. */
    B("B", "Before Opening Time"),
    A("A", "After Closing Time"),
    X("X", "Business service limitation imposed"),
    T("T", "You have been served today"),
    L("L", "Reached available token");

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
