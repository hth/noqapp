package com.noqapp.domain.types;

/**
 * hitender
 * 2/4/18 12:25 AM
 */
public enum ExternalPermissionEnum {
    P("P", "Payment Access", "Support Team NoQueue Payment Setup"),
    A("A", "Advertisement Access", "Support Team NoQueue Advertisement"),
    C("C", "Complete Access", "Support Team NoQueue");

    private final String name;
    private final String description;
    private final String customerFriendlyDescription;

    ExternalPermissionEnum(String name, String description, String customerFriendlyDescription) {
        this.name = name;
        this.description = description;
        this.customerFriendlyDescription = customerFriendlyDescription;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCustomerFriendlyDescription() {
        return customerFriendlyDescription;
    }

    @Override
    public String toString() {
        return description;
    }
}
