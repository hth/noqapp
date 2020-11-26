package com.noqapp.domain.types;

/**
 * hitender
 * 10/28/20 5:08 PM
 */
public enum QueueJoinDeniedEnum {

    /* These should not be used for FCM messages. These needs to be fixed in a separate enum. */
    A("A", "After Closing Time", "Token issued after store closing"),

    /* B is when user joins with a different time on mobile. */
    B("B", "Before Opening Time", "Token issued before store opening"),

    /* When store is closed. */
    C("C", "Closed", "Store is closed"),

    W("W", "Wait until service has begun", "Cancelled service. Please wait until service has begun to reclaim your spot if available."),
    X("X", "Business service limitation imposed" , "You have been serviced in past. Please wait until few days to issue tokens."),
    T("T", "You have been served today", "You have been served today"),
    L("L", "Reached available token", "Reached maximum number of token");

    private final String name;
    private final String description;
    private final String friendlyDescription;

    QueueJoinDeniedEnum(String name, String description, String friendlyDescription) {
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
            case A:
            case B:
            case C:
            case W:
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
