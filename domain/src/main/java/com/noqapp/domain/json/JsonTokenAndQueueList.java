package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.*;
import com.noqapp.domain.AbstractDomain;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 4/17/17 9:56 PM
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
public class JsonTokenAndQueueList extends AbstractDomain {

    @JsonProperty ("sb")
    private boolean sinceBeginning;

    @JsonProperty ("tqs")
    private List<JsonTokenAndQueue> tokenAndQueues = new ArrayList<>();

    public List<JsonTokenAndQueue> getTokenAndQueues() {
        return tokenAndQueues;
    }

    public JsonTokenAndQueueList setTokenAndQueues(List<JsonTokenAndQueue> tokenAndQueues) {
        this.tokenAndQueues = tokenAndQueues;
        return this;
    }

    public boolean isSinceBeginning() {
        return sinceBeginning;
    }

    public JsonTokenAndQueueList setSinceBeginning(boolean sinceBeginning) {
        this.sinceBeginning = sinceBeginning;
        return this;
    }

    @Override
    public String toString() {
        return "JsonTokenAndQueueList{" +
                "tokenAndQueues=" + tokenAndQueues +
                '}';
    }
}
