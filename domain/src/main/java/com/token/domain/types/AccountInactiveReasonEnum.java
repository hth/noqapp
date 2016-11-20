package com.token.domain.types;

/**
 * User: hitender
 * Date: 11/18/16 6:05 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum AccountInactiveReasonEnum {
    ANV("ACCOUNT_NOT_VALIDATED", "Account Not Validated");

    private final String name;
    private final String description;

    AccountInactiveReasonEnum(String name, String description) {
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

