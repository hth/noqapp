package com.token.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class JsonTokenQueue {
    private static final Logger LOG = LoggerFactory.getLogger(JsonTokenQueue.class);

    @JsonProperty ("c")
    private String code;

    @JsonProperty ("t")
    private int token;

    @JsonProperty ("s")
    private int servingNumber;

    public JsonTokenQueue(String code) {
        this.code = code;
    }

    public JsonTokenQueue setToken(int token) {
        this.token = token;
        return this;
    }

    public JsonTokenQueue setServingNumber(int servingNumber) {
        this.servingNumber = servingNumber;
        return this;
    }
}
