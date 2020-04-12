package com.noqapp.domain.types;

/**
 * User: hitender
 * Date: 11/25/16 11:42 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum MailTypeEnum {
    FAILURE,
    SUCCESS,
    ACCOUNT_NOT_VALIDATED,
    ACCOUNT_NOT_FOUND,
    SUCCESS_SENT_TO_ADMIN
}
