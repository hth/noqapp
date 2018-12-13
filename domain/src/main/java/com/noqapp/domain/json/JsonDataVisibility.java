package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.DataVisibilityEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

/**
 * hitender
 * 2018-12-09 09:07
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
public class JsonDataVisibility extends AbstractDomain {

    @JsonProperty("dvs")
    private Map<String, DataVisibilityEnum> dataVisibilities = new HashMap<>();

    public Map<String, DataVisibilityEnum> getDataVisibilities() {
        return dataVisibilities;
    }

    public JsonDataVisibility setDataVisibilities(Map<String, DataVisibilityEnum> dataVisibilities) {
        this.dataVisibilities = dataVisibilities;
        return this;
    }

    public JsonDataVisibility addDataVisibility(String userLevel, DataVisibilityEnum dataVisibility) {
        this.dataVisibilities.put(userLevel, dataVisibility);
        return this;
    }
}
