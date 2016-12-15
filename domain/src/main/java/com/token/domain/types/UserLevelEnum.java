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
    BIZ_MANAGER("Business Manager", 20),
    BIZ_ADMIN("Business Admin", 30),
    TECHNICIAN("Tech", 40),
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
