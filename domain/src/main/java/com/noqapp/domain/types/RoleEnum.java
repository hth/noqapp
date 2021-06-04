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

    /** A queue supervisor. */
    ROLE_Q_SUPERVISOR,

    /** A Store manager. */
    ROLE_S_MANAGER,

    /** A Business Asset Supervisor for a business. */
    ROLE_MAS,

    /** A Business bursar for a business. */
    ROLE_M_ACCOUNTANT,

    /** A Business admin for a business. */
    ROLE_M_ADMIN,

    /** Validate and process data. */
    ROLE_TECHNICIAN,

    /** Validate medical and process data. */
    ROLE_MEDICAL_TECHNICIAN,

    /** Has view access pending things for to ROLE_TECHNICIAN. */
    ROLE_SUPERVISOR,

    /** Has administrator role. */
    ROLE_ADMIN
}

