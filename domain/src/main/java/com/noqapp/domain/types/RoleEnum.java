package com.noqapp.domain.types;

/**
 * User: hitender
 * Date: 11/18/16 3:11 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum RoleEnum {

    /** Has read access. */
    ROLE_ANALYSIS,

    /** A regular user. */
    ROLE_CLIENT,

    /** A business manager. */
    ROLE_MER_MANAGER,

    /** A business admin. */
    ROLE_MER_ADMIN,

    /** Validate and process data. */
    ROLE_TECHNICIAN,

    /** Has view access pending things for to ROLE_TECHNICIAN. */
    ROLE_SUPERVISOR,

    /** Has administrator role. */
    ROLE_ADMIN
}

