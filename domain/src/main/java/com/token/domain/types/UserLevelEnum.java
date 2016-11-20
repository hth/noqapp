package com.token.domain.types;

/**
 * User: hitender
 * Date: 11/18/16 9:58 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum UserLevelEnum {
    USER("User", 10),
    ACCOUNTANT("Accountant", 11),
    ENTERPRISE("Enterprise", 20),
    BUSINESS("Business", 30),
    TECH_RECEIPT("Receipt Tech", 40),
    TECH_CAMPAIGN("Campaign Tech", 41),
    SUPERVISOR("Super", 50),
    ANALYSIS_READ("Analysis Read", 60),
    ADMIN("Admin", 90);

    private final String description;
    private final int value;

    /**
     *
     * @param description
     * @param value - used for comparing specific access
     */
    UserLevelEnum(String description, int value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return description;
    }
}
