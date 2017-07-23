package com.noqapp.domain.types;

import java.util.Arrays;
import java.util.List;

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
    CLIENT("Client", 10),
    Q_SUPERVISOR("Queue Supervisor", 22),
    S_MANAGER("Store Manager", 24),
    M_ADMIN("Merchant Admin", 29),
    TECHNICIAN("Tech", 40),
    SUPERVISOR("Super", 50),
    ANALYSIS("Analysis", 60),
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

    public static List<UserLevelEnum> merchantLevels() {
        return Arrays.asList(Q_SUPERVISOR, S_MANAGER, M_ADMIN);
    }

    @Override
    public String toString() {
        return description;
    }
}
