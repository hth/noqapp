package com.noqapp.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 5/3/17 12:39 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "FORGOT_RECOVER")
@CompoundIndexes (value = {
        @CompoundIndex (name = "forgot_recover_idx", def = "{'RID': -1, 'AUTH' : -1}", unique = true, background = true)
})
public class ForgotRecoverEntity extends BaseEntity {

    @NotNull
    @Field ("RID")
    private final String queueUserId;

    @NotNull
    @Field ("AUTH")
    private final String authenticationKey;

    private ForgotRecoverEntity(String queueUserId, String authenticationKey) {
        super();
        this.queueUserId = queueUserId;
        this.authenticationKey = authenticationKey;
    }

    public static ForgotRecoverEntity newInstance(String queueUserId, String authenticationKey) {
        return new ForgotRecoverEntity(queueUserId, authenticationKey);
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }
}