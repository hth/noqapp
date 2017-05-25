package com.noqapp.domain.json.fcm.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.QueueUserStateEnum;

/**
 * Data associated after client has been served or either was skipped for no show.
 * User: hitender
 * Date: 3/7/17 11:07 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect (
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder (alphabetic = true)
@JsonIgnoreProperties (ignoreUnknown = true)
@JsonInclude (JsonInclude.Include.NON_NULL)
public class JsonClientData extends JsonData {

    @JsonProperty ("c")
    private String codeQR;

    @JsonProperty ("u")
    private QueueUserStateEnum queueUserState;

    @JsonProperty ("o")
    private String topic;

    public JsonClientData(FirebaseMessageTypeEnum firebaseMessageType) {
        super(firebaseMessageType);
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonClientData setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public QueueUserStateEnum getQueueUserState() {
        return queueUserState;
    }

    public JsonClientData setQueueUserState(QueueUserStateEnum queueUserState) {
        this.queueUserState = queueUserState;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public JsonClientData setTopic(String topic) {
        this.topic = topic;
        return this;
    }
}
