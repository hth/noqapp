package com.noqapp.domain.types;

/**
 * hitender
 * 2/24/18 6:54 AM
 */
public enum QueueServiceEnum {
    QUE("QUE", "Queue"),
    MED("MED", "Medical"),
    PAY("PAY", "Payment");

    private final String name;
    private final String description;

    QueueServiceEnum(String name, String description) {
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
