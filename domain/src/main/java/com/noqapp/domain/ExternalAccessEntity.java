package com.noqapp.domain;

import com.noqapp.domain.types.ExternalPermissionEnum;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Base64;

import javax.validation.constraints.NotNull;

/**
 * Support access given to NoQueue Supervisor and Manager for customer business account.
 *
 * hitender
 * 2/3/18 11:49 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "EXTERNAL_ACCESS")
@CompoundIndexes(value = {
        @CompoundIndex(name = "external_access_idx", def = "{'BN': 1, 'QID': 1}", unique = true),
})
public class ExternalAccessEntity extends BaseEntity {

    @NotNull
    @Field("BN")
    private String bizId;

    @NotNull
    @Field ("QID")
    private String qid;

    /* Person who gave permission to QID. */
    @NotNull
    @Field ("AID")
    private String approverQID;

    @NotNull
    @Field ("EP")
    private ExternalPermissionEnum externalPermission = ExternalPermissionEnum.C;

    @SuppressWarnings("unused")
    public ExternalAccessEntity() {
        super();
    }

    public ExternalAccessEntity(@NotNull String bizId, @NotNull String qid) {
        this.bizId = bizId;
        this.qid = qid;
    }

    public String getBizId() {
        return bizId;
    }

    public String getQid() {
        return qid;
    }

    public String getApproverQID() {
        return approverQID;
    }

    public ExternalAccessEntity setApproverQID(String approverQID) {
        this.approverQID = approverQID;
        return this;
    }

    public ExternalPermissionEnum getExternalPermission() {
        return externalPermission;
    }

    public ExternalAccessEntity setExternalPermission(ExternalPermissionEnum externalPermission) {
        this.externalPermission = externalPermission;
        return this;
    }

    @Transient
    public String getIdAsBase64() {
        return Base64.getEncoder().encodeToString(id.getBytes());
    }
}
