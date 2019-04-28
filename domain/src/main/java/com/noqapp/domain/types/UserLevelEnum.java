package com.noqapp.domain.types;

import java.util.Arrays;
import java.util.List;

/**
 * User: hitender
 * Date: 11/18/16 9:58 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum UserLevelEnum {
    /* Regular User. */
    CLIENT("Client", 10),

    /* Maintain the order otherwise there could be bug. Client is always less than Queue Supervisor. */
    Q_SUPERVISOR("Queue Supervisor", 22),
    S_MANAGER("Store Manager", 24),
    M_ACCOUNTANT("Merchant Accountant", 26),
    M_ADMIN("Merchant Admin", 29),

    /* System User Level. */
    TECHNICIAN("Tech", 40),
    MEDICAL_TECHNICIAN("Medical Tech", 41),
    SUPERVISOR("Super", 50),
    ANALYSIS("Analysis", 60),
    ADMIN("Admin", 90);

    private final String description;
    private final int value;

    /**
     * @param description
     * @param value       - used for comparing specific access
     */
    UserLevelEnum(String description, int value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    /* Maintain the order. */
    public int getValue() {
        return value;
    }

    /**
     * M_ADMIN is no longer available to manage Queue. Nor should they be in BUSINESS_USER_STORE collection. Only
     * Q_SUPERVISOR, S_MANAGER are allowed in BUSINESS_USER_STORE. M_ADMIN can only be deleted/inactive by another
     * M_ADMIN. Once upgraded to M_ADMIN, it should be deleted from collection and given appropriate authority. M_ADMIN,
     * S_MANAGER, Q_SUPERVISOR cannot be added as a supervisor for other queues in different business.
     * Only user with ROLE as CLIENT can be added to list of Queue Managers.
     * M_ACCOUNTANT is only for payments.
     *
     * @return
     * @since 12/22/2017
     */
    public static List<UserLevelEnum> allowedBusinessUserLevel() {
        return Arrays.asList(Q_SUPERVISOR, M_ACCOUNTANT, S_MANAGER);
    }

    @Override
    public String toString() {
        return description;
    }
}
