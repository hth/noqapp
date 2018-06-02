package com.noqapp.domain;

import com.noqapp.common.utils.Validate;
import com.noqapp.domain.types.UserLevelEnum;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Contains relation of Business_Manager with specific store access.
 *
 * User: hitender
 * Date: 12/13/16 10:26 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "BUSINESS_USER_STORE")
@CompoundIndexes (value = {
        @CompoundIndex (name = "business_user_store_idx", def = "{'QID': -1, 'BS': -1, 'BN' : -1}", unique = true, background = true),
        @CompoundIndex (name = "business_user_store_qr_idx", def = "{'QID': -1, 'QR': -1}", unique = true, background = true)
})
public class BusinessUserStoreEntity extends BaseEntity {

    @NotNull
    @Field ("QID")
    private String queueUserId;

    @NotNull
    @Field ("BS")
    private String bizStoreId;

    @NotNull
    @Field ("BN")
    private String bizNameId;

    @NotNull
    @Field ("QR")
    private String codeQR;

    @NotNull
    @Field ("UL")
    private UserLevelEnum userLevel;

    @SuppressWarnings("unused")
    public BusinessUserStoreEntity() {
        super();
        //Default constructor, required to keep bean happy
    }

    public BusinessUserStoreEntity(String queueUserId, String bizStoreId, String bizNameId, String codeQR, UserLevelEnum userLevel) {
        Assertions.assertTrue(Validate.isValidQid(queueUserId), "Should be a valid qid");
        this.queueUserId = queueUserId;

        Assertions.assertTrue(Validate.isValidObjectId(bizStoreId), "Should be a valid ObjectId");
        this.bizStoreId = bizStoreId;

        Assertions.assertTrue(Validate.isValidObjectId(bizNameId), "Should be a valid ObjectId");
        this.bizNameId = bizNameId;

        this.codeQR = codeQR;
        this.userLevel = userLevel;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public UserLevelEnum getUserLevel() {
        return userLevel;
    }

    @Deprecated
    public BusinessUserStoreEntity setUserLevel(UserLevelEnum userLevel) {
        this.userLevel = userLevel;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessUserStoreEntity that = (BusinessUserStoreEntity) o;
        return Objects.equals(codeQR, that.codeQR);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codeQR);
    }
}
