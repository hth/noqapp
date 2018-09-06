package com.noqapp.domain.json.fcm.data;

import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.UUID;

/**
 * hitender
 * 8/1/18 6:43 PM
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
public class JsonAlertData extends JsonData {

    @JsonProperty("mo")
    private MessageOriginEnum messageOrigin;

    @JsonProperty("mi")
    private String messageId;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("qr")
    private String codeQR;

    JsonAlertData(FirebaseMessageTypeEnum firebaseMessageType, MessageOriginEnum messageOrigin) {
        super(firebaseMessageType);
        this.messageOrigin = messageOrigin;
        this.messageId = UUID.randomUUID().toString();
    }

    public MessageOriginEnum getMessageOrigin() {
        return messageOrigin;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonAlertData setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonAlertData setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }
}
