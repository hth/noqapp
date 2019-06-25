package com.noqapp.domain;

import com.noqapp.domain.types.CommunicationModeEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 11/18/16 6:02 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "USER_PREFERENCE")
@CompoundIndexes({
    @CompoundIndex(name = "user_preference_idx", def = "{'QID': 1}", unique = true)
})
public class UserPreferenceEntity extends BaseEntity {

    @Field("QID")
    private String queueUserId;

    @Field("PS")
    private CommunicationModeEnum promotionalSMS = CommunicationModeEnum.R;

    @Field("FN")
    private CommunicationModeEnum firebaseNotification = CommunicationModeEnum.R;

    /**
     * To make bean happy
     */
    @SuppressWarnings("unused")
    public UserPreferenceEntity() {
        super();
    }

    private UserPreferenceEntity(String queueUserId) {
        super();
        this.queueUserId = queueUserId;
    }

    public static UserPreferenceEntity newInstance(String queueUserId) {
        return new UserPreferenceEntity(queueUserId);
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public UserPreferenceEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public CommunicationModeEnum getPromotionalSMS() {
        return promotionalSMS;
    }

    public UserPreferenceEntity setPromotionalSMS(CommunicationModeEnum promotionalSMS) {
        this.promotionalSMS = promotionalSMS;
        return this;
    }

    public CommunicationModeEnum getFirebaseNotification() {
        return firebaseNotification;
    }

    public UserPreferenceEntity setFirebaseNotification(CommunicationModeEnum firebaseNotification) {
        this.firebaseNotification = firebaseNotification;
        return this;
    }
}
