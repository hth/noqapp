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
 * hitender
 * 10/5/18 10:27 AM
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonQueueHistoricalList extends AbstractDomain {

    @JsonProperty("qhs")
    private List<JsonQueueHistorical> queueHistoricals = new ArrayList<>();

    public List<JsonQueueHistorical> getQueueHistoricals() {
        return queueHistoricals;
    }

    public JsonQueueHistoricalList setQueueHistoricals(List<JsonQueueHistorical> queueHistoricals) {
        this.queueHistoricals = queueHistoricals;
        return this;
    }

    public JsonQueueHistoricalList addQueueHistorical(JsonQueueHistorical queueHistorical) {
        this.queueHistoricals.add(queueHistorical);
        return this;
    }
}
