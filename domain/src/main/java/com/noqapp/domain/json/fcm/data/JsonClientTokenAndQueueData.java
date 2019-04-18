package com.noqapp.domain.json.fcm.data;

import com.noqapp.domain.json.JsonTokenAndQueue;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-04-12 11:47
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
public class JsonClientTokenAndQueueData extends JsonData {

    @JsonProperty("mo")
    private MessageOriginEnum messageOrigin;

    /** Code QR of store that is originating message. */
    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty ("tqs")
    private List<JsonTokenAndQueue> tokenAndQueues = new ArrayList<>();

    public JsonClientTokenAndQueueData(FirebaseMessageTypeEnum firebaseMessageType, MessageOriginEnum messageOrigin) {
        super(firebaseMessageType);
        this.messageOrigin = messageOrigin;
    }

    public MessageOriginEnum getMessageOrigin() {
        return messageOrigin;
    }

    public JsonClientTokenAndQueueData setMessageOrigin(MessageOriginEnum messageOrigin) {
        this.messageOrigin = messageOrigin;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonClientTokenAndQueueData setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public List<JsonTokenAndQueue> getTokenAndQueues() {
        return tokenAndQueues;
    }

    public JsonClientTokenAndQueueData setTokenAndQueues(List<JsonTokenAndQueue> tokenAndQueues) {
        this.tokenAndQueues = tokenAndQueues;
        return this;
    }
}
