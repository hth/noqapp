package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 12/19/17 8:14 PM
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
public class JsonQueueList extends AbstractDomain {

    @JsonProperty("cs")
    private List<JsonCategory> categories = new ArrayList<>();

    @JsonProperty("qs")
    private List<JsonQueue> queues = new ArrayList<>();

    public List<JsonCategory> getCategories() {
        return categories;
    }

    public JsonQueueList addCategories(JsonCategory category) {
        this.categories.add(category);
        return this;
    }

    public List<JsonQueue> getQueues() {
        return queues;
    }

    public JsonQueueList addQueues(JsonQueue queue) {
        this.queues.add(queue);
        return this;
    }
}
