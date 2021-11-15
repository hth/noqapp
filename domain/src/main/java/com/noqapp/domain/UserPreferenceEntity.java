package com.noqapp.domain;

import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.CommunicationModeEnum;
import com.noqapp.domain.types.DeliveryModeEnum;
import com.noqapp.domain.types.PaymentMethodEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    /** BizStore codeQR when marked favorite by user. */
    @Field("FT")
    private List<String> favoriteTagged = new ArrayList<>();

    /** BizStore codeQR for recent. */
    @Field("FS")
    private List<String> favoriteSuggested = new ArrayList<>();

    /** SOS receivers. */
    @Field("SQ")
    private Set<String> sosReceiverQids = new HashSet<>();

    /** Net point earned. */
    @Field("EP")
    private int earnedPoint = 0;

    @Field("EPP")
    private int earnedPointPreviously= 0;

    /** To make bean happy. */
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

    public List<String> getFavoriteTagged() {
        return favoriteTagged;
    }

    @Mobile
    public UserPreferenceEntity addFavoriteTagged(String favoriteTagged) {
        this.favoriteTagged.add(0, favoriteTagged);

        if (this.favoriteTagged.size() > 10) {
            this.favoriteTagged.remove(this.favoriteTagged.size() - 1);
        }
        return this;
    }

    public List<String> getFavoriteSuggested() {
        return favoriteSuggested;
    }

    public UserPreferenceEntity addFavoriteSuggested(String favoriteSuggested) {
        this.favoriteSuggested.add(0, favoriteSuggested);

        if (this.favoriteSuggested.size() > 10) {
            this.favoriteSuggested.remove(this.favoriteSuggested.size() - 1);
        }
        return this;
    }

    public Set<String> getSosReceiverQids() {
        return sosReceiverQids;
    }

    public UserPreferenceEntity addSosReceiverQid(String sosReceiverQid) {
        this.sosReceiverQids.add(sosReceiverQid);
        return this;
    }

    public int getEarnedPoint() {
        return earnedPoint;
    }

    public int getEarnedPointPreviously() {
        return earnedPointPreviously;
    }
}
