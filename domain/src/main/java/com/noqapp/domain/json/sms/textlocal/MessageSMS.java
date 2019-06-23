package com.noqapp.domain.json.sms.textlocal;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 2019-06-24 00:38
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
public class MessageSMS extends AbstractDomain {

    @JsonProperty("num_parts")
    private String numParts;

    @JsonProperty("sender")
    private String sender;

    @JsonProperty("content")
    private String content;

    public String getNumParts() {
        return numParts;
    }

    public MessageSMS setNumParts(String numParts) {
        this.numParts = numParts;
        return this;
    }

    public String getSender() {
        return sender;
    }

    public MessageSMS setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public String getContent() {
        return content;
    }

    public MessageSMS setContent(String content) {
        this.content = content;
        return this;
    }
}
