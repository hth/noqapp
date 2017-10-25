package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.*;
import com.noqapp.domain.TokenQueueEntity;

/**
 * User: hitender
 * Date: 2/26/17 5:13 PM
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
public class JsonTopic extends JsonToken {

    @JsonProperty ("o")
    private String topic;

    private JsonTopic() {
        super();
    }

    public JsonTopic(TokenQueueEntity tokenQueue) {
        super(tokenQueue);
        this.topic = tokenQueue.getTopic();
    }
}
