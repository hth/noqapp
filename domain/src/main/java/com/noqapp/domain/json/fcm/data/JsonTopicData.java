package com.noqapp.domain.json.fcm.data;

import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hitender
 * 7/31/18 5:36 PM
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
public class JsonTopicData {
    private static final Logger LOG = LoggerFactory.getLogger(JsonTopicData.class);

    private JsonTopicQueueData jsonTopicQueueData;
    private JsonTopicOrderData jsonTopicOrderData;
    private JsonTopicAppointmentData jsonTopicAppointmentData;
    private JsonAlertData jsonAlertData;
    private JsonMedicalFollowUp jsonMedicalFollowUp;

    /**
     * When FirebaseMessageTypeEnum.P is Personal for Merchant, it saves in DB otherwise it processes the message it receives.
     *
     * @param messageOrigin
     * @param firebaseMessageType
     */
    public JsonTopicData(MessageOriginEnum messageOrigin, FirebaseMessageTypeEnum firebaseMessageType) {
        switch (messageOrigin) {
            case Q:
                jsonTopicQueueData = new JsonTopicQueueData(firebaseMessageType, messageOrigin);
                break;
            case QA:
                jsonTopicAppointmentData = new JsonTopicAppointmentData(firebaseMessageType, messageOrigin);
                break;
            case O:
                jsonTopicOrderData = new JsonTopicOrderData(firebaseMessageType, messageOrigin);
                break;
            case D:
                jsonAlertData = new JsonAlertData(firebaseMessageType, messageOrigin);
                break;
            case A:
                jsonAlertData = new JsonAlertData(firebaseMessageType, messageOrigin);
                break;
            case MF:
                jsonMedicalFollowUp = new JsonMedicalFollowUp(firebaseMessageType, messageOrigin);
                break;
            default:
                LOG.error("Reached unreachable condition {}", messageOrigin);
                throw new UnsupportedOperationException("Reached unreachable condition");
        }
    }

    public JsonTopicQueueData getJsonTopicQueueData() {
        return jsonTopicQueueData;
    }

    public JsonTopicData setJsonTopicQueueData(JsonTopicQueueData jsonTopicQueueData) {
        this.jsonTopicQueueData = jsonTopicQueueData;
        return this;
    }

    public JsonTopicOrderData getJsonTopicOrderData() {
        return jsonTopicOrderData;
    }

    public JsonTopicData setJsonTopicOrderData(JsonTopicOrderData jsonTopicOrderData) {
        this.jsonTopicOrderData = jsonTopicOrderData;
        return this;
    }

    public JsonTopicAppointmentData getJsonTopicAppointmentData() {
        return jsonTopicAppointmentData;
    }

    public JsonTopicData setJsonTopicAppointmentData(JsonTopicAppointmentData jsonTopicAppointmentData) {
        this.jsonTopicAppointmentData = jsonTopicAppointmentData;
        return this;
    }

    public JsonAlertData getJsonAlertData() {
        return jsonAlertData;
    }

    public JsonTopicData setJsonAlertData(JsonAlertData jsonAlertData) {
        this.jsonAlertData = jsonAlertData;
        return this;
    }

    public JsonMedicalFollowUp getJsonMedicalFollowUp() {
        return jsonMedicalFollowUp;
    }

    public JsonTopicData setJsonMedicalFollowUp(JsonMedicalFollowUp jsonMedicalFollowUp) {
        this.jsonMedicalFollowUp = jsonMedicalFollowUp;
        return this;
    }
}
