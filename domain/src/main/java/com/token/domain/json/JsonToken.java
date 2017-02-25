package com.token.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.token.domain.AbstractDomain;
import com.token.domain.TokenQueueEntity;

/**
 * User: hitender
 * Date: 11/18/16 11:56 AM
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
public class JsonToken extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonToken.class);

    @JsonProperty ("c")
    private String codeQR;

    @JsonProperty ("t")
    private int totalToken;

    @JsonProperty ("s")
    private int servingNumber;

    @JsonProperty ("d")
    private String displayName;

    @JsonProperty ("a")
    private boolean active;

    public JsonToken(TokenQueueEntity tokenQueue) {
        this.codeQR = tokenQueue.getId();
        this.totalToken = tokenQueue.getLastNumber();
        this.servingNumber = tokenQueue.getCurrentlyServing();
        this.displayName = tokenQueue.getDisplayName();
        this.active = tokenQueue.isActive();
    }

    public JsonToken(String codeQR) {
        this.codeQR = codeQR;
    }

    public JsonToken setTotalToken(int totalToken) {
        this.totalToken = totalToken;
        return this;
    }

    public JsonToken setServingNumber(int servingNumber) {
        this.servingNumber = servingNumber;
        return this;
    }

    public JsonToken setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public JsonToken setActive(boolean active) {
        this.active = active;
        return this;
    }
}
