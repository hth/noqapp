package com.noqapp.domain.json;

import static org.apiguardian.api.API.Status.DEPRECATED;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apiguardian.api.API;

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

    @API(status = DEPRECATED, since = "1.3.122")
    @Deprecated
    @JsonProperty ("sb")
    private boolean sinceBeginning;

    @JsonProperty ("tqs")
    private List<JsonTokenAndQueue> tokenAndQueues = new ArrayList<>();

    @JsonProperty ("jsl")
    private JsonScheduleList jsonScheduleList = new JsonScheduleList();

    public boolean isSinceBeginning() {
        return sinceBeginning;
    }

    public JsonTokenAndQueueList setSinceBeginning(boolean sinceBeginning) {
        this.sinceBeginning = sinceBeginning;
        return this;
    }

    public List<JsonTokenAndQueue> getTokenAndQueues() {
        return tokenAndQueues;
    }

    public JsonTokenAndQueueList setTokenAndQueues(List<JsonTokenAndQueue> tokenAndQueues) {
        this.tokenAndQueues = tokenAndQueues;
        return this;
    }

    public JsonScheduleList getJsonScheduleList() {
        return jsonScheduleList;
    }

    public JsonTokenAndQueueList setJsonScheduleList(JsonScheduleList jsonScheduleList) {
        this.jsonScheduleList = jsonScheduleList;
        return this;
    }

    @Override
    public String toString() {
        return "JsonTokenAndQueueList{" +
                "tokenAndQueues=" + tokenAndQueues +
                '}';
    }
}
