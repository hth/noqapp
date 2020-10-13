package com.noqapp.domain.types;

/**
 * User: hitender
 * Date: 3/2/17 11:28 AM
 */
public enum QueueStatusEnum {
    S("S", "Start", ""),
    R("R", "Re-Start", ""),
    N("N", "Next", ""),
    /* Pause is a status for Server/Merchant, Pause applies to just Server/Merchant. Queue never pauses. */
    P("P", "Pause", ""),
    D("D", "Done", ""),
    C("C", "Closed", ""),

    /* These should not be used for FCM messages. These needs to be fixed in a separate enum. */
    /* B is when user joins with a different time on mobile. */
    B("B", "Before Opening Time", "Token issued before store opening"),
    A("A", "After Closing Time", "Token issued after store closing"),
    X("X", "Business service limitation imposed" , "You have been serviced in past. Please wait until few days to issue tokens"),
    T("T", "You have been served today", "You have been served today"),
    L("L", "Reached available token", "Reached maximum number of token");

    private final String name;
    private final String description;
    private final String friendlyDescription;

    QueueStatusEnum(String name, String description, String friendlyDescription) {
        this.name = name;
        this.description = description;
        this.friendlyDescription = friendlyDescription;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String friendlyDescription() {
        String reason;

        switch (this) {
            case B:
            case A:
            case X:
            case L:
                reason = this.friendlyDescription;
                break;
            default:
                reason = "Due to some limitation token was not issued";
                break;
        }

        return reason;
    }

    @Override
    public String toString() {
        return description;
    }
}
