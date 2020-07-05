package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.OnOffEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * hitender
 * 5/24/20 4:35 PM
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
public class JsonBusinessFeatures extends AbstractDomain {

    @JsonProperty("pa")
    private OnOffEnum priorityAccess;

    @JsonProperty("sd")
    private int limitServiceByDays;

    public OnOffEnum getPriorityAccess() {
        return priorityAccess;
    }

    public JsonBusinessFeatures setPriorityAccess(OnOffEnum priorityAccess) {
        this.priorityAccess = priorityAccess;
        return this;
    }

    public int getLimitServiceByDays() {
        return limitServiceByDays;
    }

    public JsonBusinessFeatures setLimitServiceByDays(int limitServiceByDays) {
        this.limitServiceByDays = limitServiceByDays;
        return this;
    }
}
