package com.noqapp.domain.json.tv;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 2018-12-17 18:27
 */
@SuppressWarnings ({
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
public class JsonQueueTVList extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonQueueTVList.class);

    @JsonProperty("qs")
    private List<JsonQueueTV> queues = new ArrayList<>();

    public List<JsonQueueTV> getQueues() {
        return queues;
    }

    public JsonQueueTVList setQueues(List<JsonQueueTV> queues) {
        this.queues = queues;
        return this;
    }

    public JsonQueueTVList addQueue(JsonQueueTV queue) {
        this.queues.add(queue);
        return this;
    }
}
