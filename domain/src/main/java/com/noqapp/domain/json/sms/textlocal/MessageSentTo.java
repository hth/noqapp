package com.noqapp.domain.json.sms.textlocal;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 2019-06-24 00:44
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
public class MessageSentTo extends AbstractDomain {

    @JsonProperty("id")
    private String id;

    @JsonProperty("recipient")
    private String recipient;

    public String getId() {
        return id;
    }

    public MessageSentTo setId(String id) {
        this.id = id;
        return this;
    }

    public String getRecipient() {
        return recipient;
    }

    public MessageSentTo setRecipient(String recipient) {
        this.recipient = recipient;
        return this;
    }
}
