package com.token.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.token.domain.AbstractDomain;

/**
 * User: hitender
 * Date: 1/1/17 7:04 AM
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
public class JsonTopicMessage extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonTopicMessage.class);

    @JsonProperty ("to")
    private String to;

    @JsonProperty ("data")
    private JsonTopicData data;

    /**
     *
     * @param to        topic
     * @param message   message
     */
    public JsonTopicMessage(String to, String message) {
        this.to = to;
        this.data = new JsonTopicData(message);
    }
}
