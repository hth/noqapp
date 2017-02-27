package com.token.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.token.domain.AbstractDomain;

/**
 * User: hitender
 * Date: 2/27/17 12:14 PM
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
public class JsonTokenAndQueue extends AbstractDomain {

    private JsonToken jsonToken;
    private JsonQueue jsonQueue;

    public JsonTokenAndQueue(JsonToken jsonToken, JsonQueue jsonQueue) {
        this.jsonToken = jsonToken;
        this.jsonQueue = jsonQueue;
    }

    public JsonToken getJsonToken() {
        return jsonToken;
    }

    public void setJsonToken(JsonToken jsonToken) {
        this.jsonToken = jsonToken;
    }

    public JsonQueue getJsonQueue() {
        return jsonQueue;
    }

    public void setJsonQueue(JsonQueue jsonQueue) {
        this.jsonQueue = jsonQueue;
    }
}
