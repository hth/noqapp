package com.token.domain.types;

/**
 * User: hitender
 * Date: 12/16/16 3:57 PM
 */
public enum QueueStateEnum {
    Q("Q", "Queued"),
    N("N", "No Show"),
    A("A", "Abort"),
    S("S", "Serviced");

    private final String name;
    private final String description;

    QueueStateEnum(String name, String description) {
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
