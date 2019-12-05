package com.noqapp.domain.json.fcm.data;

import com.noqapp.domain.json.fcm.data.speech.JsonAudioConfig;
import com.noqapp.domain.json.fcm.data.speech.JsonTextInput;
import com.noqapp.domain.json.fcm.data.speech.JsonTextToSpeech;
import com.noqapp.domain.json.fcm.data.speech.JsonVoiceInput;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.QueueStatusEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.UUID;

/**
 * hitender
 * 7/31/18 5:48 PM
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
public class JsonTopicOrderData extends JsonData {

    @JsonProperty("mo")
    private MessageOriginEnum messageOrigin;

    @JsonProperty("message")
    private String message;

    @JsonProperty("ln")
    private int lastNumber;

    @JsonProperty("cs")
    private int currentlyServing;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("q")
    private QueueStatusEnum queueStatus;

    @JsonProperty("g")
    private String goTo;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("os")
    private PurchaseOrderStateEnum purchaseOrderState;

    @JsonProperty("mi")
    private String messageId;

    @JsonProperty("textToSpeech")
    private JsonTextToSpeech jsonTextToSpeech;

    public JsonTopicOrderData(FirebaseMessageTypeEnum firebaseMessageType, MessageOriginEnum messageOrigin) {
        super(firebaseMessageType);
        this.messageOrigin = messageOrigin;
        this.messageId = UUID.randomUUID().toString();
    }

    public String getMessage() {
        return message;
    }

    public JsonTopicOrderData setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public JsonTopicOrderData setLastNumber(int lastNumber) {
        this.lastNumber = lastNumber;
        return this;
    }

    public int getCurrentlyServing() {
        return currentlyServing;
    }

    public JsonTopicOrderData setCurrentlyServing(int currentlyServing) {
        this.currentlyServing = currentlyServing;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonTopicOrderData setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public JsonTopicOrderData setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
        return this;
    }

    public String getGoTo() {
        return goTo;
    }

    public JsonTopicOrderData setGoTo(String goTo) {
        this.goTo = goTo;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonTopicOrderData setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public PurchaseOrderStateEnum getPurchaseOrderState() {
        return purchaseOrderState;
    }

    public JsonTopicOrderData setPurchaseOrderState(PurchaseOrderStateEnum purchaseOrderState) {
        this.purchaseOrderState = purchaseOrderState;
        return this;
    }

    public JsonTextToSpeech getTextToSpeech() {
        jsonTextToSpeech = new JsonTextToSpeech()
            .setJsonTextInput(new JsonTextInput(message))
            .setJsonVoiceInput(new JsonVoiceInput())
            .setJsonAudioConfig(new JsonAudioConfig());

        return jsonTextToSpeech;
    }
}
