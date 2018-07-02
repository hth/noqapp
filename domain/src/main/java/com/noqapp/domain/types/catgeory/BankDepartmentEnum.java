package com.noqapp.domain.types.catgeory;

/**
 * User: hitender
 * Date: 7/2/18 12:16 PM
 */
public enum BankDepartmentEnum {
    OTH("OTH", "Cheque/Withdrawal/Deposit"),
    LON("LON", "Loan"),
    ACL("ACL", "Account Link");

    private final String description;
    private final String name;

    BankDepartmentEnum(String name, String description) {
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
