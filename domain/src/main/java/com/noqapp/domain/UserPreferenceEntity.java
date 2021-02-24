package com.noqapp.domain;

import com.noqapp.domain.types.CommunicationModeEnum;
import com.noqapp.domain.types.DeliveryModeEnum;
import com.noqapp.domain.types.PaymentMethodEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

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
    private CommunicationModeEnum promotionalSMS;

    @Field("FN")
    private CommunicationModeEnum firebaseNotification;

    @Field("DM")
    private DeliveryModeEnum deliveryMode;

    @Field("PM")
    private PaymentMethodEnum paymentMethod;

    @Field("UAI")
    private String userAddressId;

    /* For auto subscribing to these topics. */
    @Field("ST")
    private Set<String> subscriptionTopics = new HashSet<>();

    /** Store codeQR. */
    @Field("FT")
    private Set<String> favoriteTagged = new HashSet<>();

    /** Store codeQR. */
    @Field("FS")
    private Set<String> favoriteSuggested = new HashSet<>();

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

    public DeliveryModeEnum getDeliveryMode() {
        return deliveryMode;
    }

    public UserPreferenceEntity setDeliveryMode(DeliveryModeEnum deliveryMode) {
        this.deliveryMode = deliveryMode;
        return this;
    }

    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    public UserPreferenceEntity setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public String getUserAddressId() {
        return userAddressId;
    }

    public UserPreferenceEntity setUserAddressId(String userAddressId) {
        this.userAddressId = userAddressId;
        return this;
    }

    public Set<String> getSubscriptionTopics() {
        return subscriptionTopics;
    }

    public UserPreferenceEntity addSubscriptionTopic(String subscriptionTopic) {
        this.subscriptionTopics.add(subscriptionTopic);
        return this;
    }

    public Set<String> getFavoriteTagged() {
        return favoriteTagged;
    }

    public UserPreferenceEntity addFavoriteTagged(String favoriteTagged) {
        this.favoriteTagged.add(favoriteTagged);
        return this;
    }

    public Set<String> getFavoriteSuggested() {
        return favoriteSuggested;
    }

    public UserPreferenceEntity addFavoriteSuggested(String favoriteSuggested) {
        this.favoriteSuggested.add(favoriteSuggested);
        return this;
    }
}
