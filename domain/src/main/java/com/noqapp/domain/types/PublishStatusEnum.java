package com.noqapp.domain.types;

/**
 * User: hitender
 * Date: 11/30/19 1:04 AM
 */
public enum PublishStatusEnum {
    I("I", "Incomplete"),
    P("P", "Pending Approval"),
    A("A", "Approved"),
    U("U", "Un-publish"),
    R("R", "Rejected"),
    D("D", "Delete");

    private String name;
    private String description;

    PublishStatusEnum(String name, String description) {
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
