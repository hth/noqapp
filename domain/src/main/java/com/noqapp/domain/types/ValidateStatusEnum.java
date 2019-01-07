package com.noqapp.domain.types;

/**
 * hitender
 * 2019-01-02 22:52
 */
public enum ValidateStatusEnum {
    I("I", "Incomplete"),
    P("P", "Pending Approval"),
    A("A", "Approved"),
    R("R", "Rejected"),
    D("D", "Delete");

    private String name;
    private String description;

    ValidateStatusEnum(String name, String description) {
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
