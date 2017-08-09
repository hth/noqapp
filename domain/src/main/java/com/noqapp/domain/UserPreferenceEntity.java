package com.noqapp.domain;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 11/18/16 6:02 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "USER_PREFERENCE")
public class UserPreferenceEntity extends BaseEntity {

    @NotNull
    @Field ("RID")
    private String queueUserId;

    @DBRef
    @Indexed (unique = true)
    @Field ("USER_PROFILE")
    private UserProfileEntity userProfile;

    /**
     * To make bean happy
     */
    @SuppressWarnings ("unused")
    private UserPreferenceEntity() {
        super();
    }

    // @PersistenceConstructor
    private UserPreferenceEntity(UserProfileEntity userProfile) {
        super();
        this.userProfile = userProfile;
        this.queueUserId = userProfile.getQueueUserId();
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    /**
     * This method is used when the Entity is created for the first time.
     *
     * @param userProfile
     * @return
     */
    public static UserPreferenceEntity newInstance(UserProfileEntity userProfile) {
        return new UserPreferenceEntity(userProfile);
    }

    public UserProfileEntity getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfileEntity userProfile) {
        this.userProfile = userProfile;
    }
}
