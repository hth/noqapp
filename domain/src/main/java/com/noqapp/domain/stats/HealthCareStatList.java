package com.noqapp.domain.stats;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 6/6/18 3:26 PM
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
public class HealthCareStatList extends AbstractDomain {

    @JsonProperty("hcs")
    private List<HealthCareStat> healthCareStat = new ArrayList<>();

    public List<HealthCareStat> getHealthCareStat() {
        return healthCareStat;
    }

    public HealthCareStatList setHealthCareStat(List<HealthCareStat> healthCareStat) {
        this.healthCareStat = healthCareStat;
        return this;
    }

    public HealthCareStatList addHealthCareStat(HealthCareStat healthCareStat) {
        this.healthCareStat.add(healthCareStat);
        return this;
    }
}
