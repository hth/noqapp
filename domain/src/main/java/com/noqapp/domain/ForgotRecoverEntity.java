package com.noqapp.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

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
    @CompoundIndex (name = "forgot_recover_idx", def = "{'QID': -1, 'AUTH' : -1}", unique = true, background = true)
})
public class ForgotRecoverEntity extends BaseEntity {

    @NotNull
    @Field ("QID")
    private String queueUserId;

    @NotNull
    @Field ("AUTH")
    private String authenticationKey;

    /*
     * Field "CD" is redundant field on which index exists to just for the sake of
     * removing records after stipulated time of 7 days.
     */
    @NotNull
    @Indexed (name = "forgot_recover_auto_expire_idx", expireAfterSeconds = 604800)
    @Field ("CD")
    private Date createDate = getCreated();

    @SuppressWarnings("unused")
    public ForgotRecoverEntity() {
        //Default constructor, required to keep bean happy
    }

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
