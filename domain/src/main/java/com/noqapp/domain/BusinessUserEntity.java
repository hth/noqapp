package com.noqapp.domain;

import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.UserLevelEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import org.junit.jupiter.api.Assertions;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 11/23/16 4:37 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "BUSINESS_USER")
@CompoundIndexes (value = {
        @CompoundIndex (name = "business_user_idx", def = "{'QID': -1}", unique = true),
})
public class BusinessUserEntity extends BaseEntity {

    @NotNull
    @Field ("QID")
    private String queueUserId;

    @NotNull
    @Field ("RS")
    private BusinessUserRegistrationStatusEnum businessUserRegistrationStatus;

    @NotNull
    @Field ("VB")
    private String validateByQid;

    @DBRef
    @Field ("B_N")
    private BizNameEntity bizName;

    /* Set the kind of business is registered as. */
    @Field ("UL")
    private UserLevelEnum userLevel;

    @Field ("EID")
    private String externalAccessId;

    @SuppressWarnings("unused")
    public BusinessUserEntity() {
        super();
    }

    private BusinessUserEntity(String queueUserId, UserLevelEnum userLevel) {
        super();
        this.queueUserId = queueUserId;
        this.userLevel = userLevel;

        /* When creating this record we are defaulting to Incomplete status. */
        this.businessUserRegistrationStatus = BusinessUserRegistrationStatusEnum.I;
    }

    public static BusinessUserEntity newInstance(String queueUserId, UserLevelEnum userLevel) {
        return new BusinessUserEntity(queueUserId, userLevel);
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public BusinessUserEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public BusinessUserRegistrationStatusEnum getBusinessUserRegistrationStatus() {
        return businessUserRegistrationStatus;
    }

    public BusinessUserEntity setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum businessUserRegistrationStatus) {
        this.businessUserRegistrationStatus = businessUserRegistrationStatus;
        return this;
    }

    public String getValidateByQid() {
        return validateByQid;
    }

    public BusinessUserEntity setValidateByQid(String validateByQid) {
        this.validateByQid = validateByQid;
        return this;
    }

    public BizNameEntity getBizName() {
        return bizName;
    }

    public BusinessUserEntity setBizName(BizNameEntity bizName) {
        this.bizName = bizName;
        return this;
    }

    public UserLevelEnum getUserLevel() {
        return userLevel;
    }

    public String getExternalAccessId() {
        return externalAccessId;
    }

    public BusinessUserEntity setExternalAccessId(ExternalAccessEntity externalAccess) {
        Assertions.assertNotNull(externalAccess.getId());
        this.externalAccessId = externalAccess.getId();
        return this;
    }
}
