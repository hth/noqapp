package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.UserPreferenceEntity;
import com.noqapp.domain.types.CommunicationModeEnum;
import com.noqapp.domain.types.DeliveryModeEnum;
import com.noqapp.domain.types.PaymentMethodEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 2019-06-25 08:18
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable",
    "unused"
})
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonUserPreference extends AbstractDomain {

    @JsonProperty("ps")
    private CommunicationModeEnum promotionalSMS;

    @JsonProperty("fn")
    private CommunicationModeEnum firebaseNotification;

    @JsonProperty("dm")
    private DeliveryModeEnum deliveryMode;

    @JsonProperty("pm")
    private PaymentMethodEnum paymentMethod;

    @JsonProperty("uai")
    private String userAddressId;

    public CommunicationModeEnum getPromotionalSMS() {
        return promotionalSMS;
    }

    public JsonUserPreference setPromotionalSMS(CommunicationModeEnum promotionalSMS) {
        this.promotionalSMS = promotionalSMS;
        return this;
    }

    public CommunicationModeEnum getFirebaseNotification() {
        return firebaseNotification;
    }

    public JsonUserPreference setFirebaseNotification(CommunicationModeEnum firebaseNotification) {
        this.firebaseNotification = firebaseNotification;
        return this;
    }

    public DeliveryModeEnum getDeliveryMode() {
        return deliveryMode;
    }

    public JsonUserPreference setDeliveryMode(DeliveryModeEnum deliveryMode) {
        this.deliveryMode = deliveryMode;
        return this;
    }

    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    public JsonUserPreference setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public String getUserAddressId() {
        return userAddressId;
    }

    public JsonUserPreference setUserAddressId(String userAddressId) {
        this.userAddressId = userAddressId;
        return this;
    }

    public static JsonUserPreference convertToJsonUserPreference(UserPreferenceEntity userPreference) {
        return new JsonUserPreference()
            .setPromotionalSMS(userPreference.getPromotionalSMS())
            .setFirebaseNotification(userPreference.getFirebaseNotification())
            .setDeliveryMode(userPreference.getDeliveryMode())
            .setPaymentMethod(userPreference.getPaymentMethod())
            .setUserAddressId(userPreference.getUserAddressId());
    }
}
