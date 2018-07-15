package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.medical.BloodTypeEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * hitender
 * 5/25/18 8:44 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "USER_MEDICAL_PROFILE")
@CompoundIndexes(value = {
        @CompoundIndex(name = "user_medical_profile_idx", def = "{'QID' : 1}", unique = true),
})
public class UserMedicalProfileEntity extends BaseEntity {

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("BT")
    private BloodTypeEnum bloodType;

    //Height in cms
    @Field("HT")
    private int height;

    public UserMedicalProfileEntity(@NotNull String queueUserId) {
        this.queueUserId = queueUserId;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public BloodTypeEnum getBloodType() {
        return bloodType;
    }

    public UserMedicalProfileEntity setBloodType(BloodTypeEnum bloodType) {
        this.bloodType = bloodType;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public UserMedicalProfileEntity setHeight(int height) {
        this.height = height;
        return this;
    }
}
