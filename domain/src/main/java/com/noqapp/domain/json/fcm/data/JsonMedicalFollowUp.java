package com.noqapp.domain.json.fcm.data;

import static com.noqapp.common.utils.AbstractDomain.ISO8601_FMT;

import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * hitender
 * 2018-11-27 14:32
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
public class JsonMedicalFollowUp extends JsonData {

    @JsonProperty("mo")
    private MessageOriginEnum messageOrigin;

    @JsonProperty("mi")
    private String messageId;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty ("qid")
    private String queueUserId;

    @JsonProperty ("pa")
    private String popFollowUpAlert;

    @JsonProperty ("fd")
    private String followUpDay;

    JsonMedicalFollowUp(FirebaseMessageTypeEnum firebaseMessageType, MessageOriginEnum messageOrigin) {
        super(firebaseMessageType);
        this.messageOrigin = messageOrigin;
        this.messageId = UUID.randomUUID().toString();
    }

    public MessageOriginEnum getMessageOrigin() {
        return messageOrigin;
    }

    public JsonMedicalFollowUp setMessageOrigin(MessageOriginEnum messageOrigin) {
        this.messageOrigin = messageOrigin;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonMedicalFollowUp setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonMedicalFollowUp setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getPopFollowUpAlert() {
        return popFollowUpAlert;
    }

    public JsonMedicalFollowUp setPopFollowUpAlert(Date popFollowUpAlert) {
        this.popFollowUpAlert = DateFormatUtils.format(popFollowUpAlert, ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        return this;
    }

    public String getFollowUpDay() {
        return followUpDay;
    }

    public JsonMedicalFollowUp setFollowUpDay(Date followUpDay) {
        this.followUpDay = DateFormatUtils.format(followUpDay, ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        return this;
    }
}
