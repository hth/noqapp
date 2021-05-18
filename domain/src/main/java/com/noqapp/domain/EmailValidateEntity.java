package com.noqapp.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 11/25/16 10:04 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "EMAIL_VALIDATE")
@CompoundIndexes (value = {
    @CompoundIndex (name = "email_valid_idx", def = "{'AUTH': 1}", unique = true),
})
public class EmailValidateEntity extends BaseEntity {

    @NotNull
    @Field ("QID")
    private String queueUserId;

    @NotNull
    @Field ("EM")
    private String email;

    @NotNull
    @Field ("AUTH")
    private String authenticationKey;

    @SuppressWarnings("unused")
    public EmailValidateEntity() {
        super();
    }

    private EmailValidateEntity(String queueUserId, String email, String authenticationKey) {
        super();
        this.queueUserId = queueUserId;
        this.email = email;
        this.authenticationKey = authenticationKey;
    }

    public static EmailValidateEntity newInstance(String queueUserId, String email, String authenticationKey) {
        return new EmailValidateEntity(queueUserId, email, authenticationKey);
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public String getEmail() {
        return email;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }
}
