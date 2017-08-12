package com.noqapp.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.UserLevelEnum;

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
    private String validateByRid;

    @DBRef
    @Field ("B_N")
    private BizNameEntity bizName;

    /* Set the kind of business is registered as. */
    @Field ("UL")
    private UserLevelEnum userLevel;

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

    public String getValidateByRid() {
        return validateByRid;
    }

    public BusinessUserEntity setValidateByRid(String validateByRid) {
        this.validateByRid = validateByRid;
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
}
