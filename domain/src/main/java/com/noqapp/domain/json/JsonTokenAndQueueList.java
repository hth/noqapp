package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 4/17/17 9:56 PM
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
public class JsonTokenAndQueueList extends AbstractDomain {

    @JsonProperty("tqs")
    private List<JsonTokenAndQueue> tokenAndQueues = new ArrayList<>();

    @JsonProperty("jsl")
    private JsonScheduleList jsonScheduleList = new JsonScheduleList();

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
